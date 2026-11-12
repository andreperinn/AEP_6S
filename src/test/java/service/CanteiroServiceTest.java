package service;

import enums.Status;
import exception.RecursoEmUsoException;
import exception.ReferenciaInvalidaException;
import model.Canteiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CanteiroRepository;
import repository.EscalaRepository;
import repository.HortaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CanteiroServiceTest {

    @Mock
    private CanteiroRepository repository;

    @Mock
    private HortaRepository hortaRepository;

    @Mock
    private EscalaRepository escalaRepository;

    @InjectMocks
    private CanteiroService service;

    private Canteiro canteiro;

    @BeforeEach
    void setUp() {
        canteiro = new Canteiro(
                "1",
                "Canteiro 1",
                "h1",
                "Alface",
                "Maria",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 15),
                Status.EM_CULTIVO
        );
    }

    @Test
    void cadastrar_deveSalvarERetornarCanteiro() {
        when(hortaRepository.existsById("h1")).thenReturn(true);
        when(repository.save(canteiro)).thenReturn(canteiro);

        Canteiro resultado = service.cadastrar(canteiro);

        assertEquals(canteiro, resultado);
        verify(repository, times(1)).save(canteiro);
    }

    @Test
    void cadastrar_deveLancarExcecaoQuandoHortaNaoExiste() {
        when(hortaRepository.existsById("h1")).thenReturn(false);

        assertThrows(ReferenciaInvalidaException.class, () -> service.cadastrar(canteiro));
        verify(repository, never()).save(any());
    }

    @Test
    void cadastrar_deveLancarExcecaoQuandoHortaNaoInformada() {
        canteiro.setHortaId(null);

        assertThrows(ReferenciaInvalidaException.class, () -> service.cadastrar(canteiro));
        verify(repository, never()).save(any());
    }

    @Test
    void listarTodos_deveRetornarListaDeCanteiros() {
        when(repository.findAll()).thenReturn(List.of(canteiro));

        List<Canteiro> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals(canteiro, resultado.get(0));
        verify(repository, times(1)).findAll();
    }

    @Test
    void listarTodos_deveRetornarListaVaziaQuandoNaoHaCanteiros() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    void listarPorHorta_deveRetornarCanteirosDaHorta() {
        when(repository.findByHortaId("h1")).thenReturn(List.of(canteiro));

        List<Canteiro> resultado = service.listarPorHorta("h1");

        assertEquals(1, resultado.size());
        assertEquals("h1", resultado.get(0).getHortaId());
    }

    @Test
    void buscarPorId_deveRetornarCanteiroQuandoExiste() {
        when(repository.findById("1")).thenReturn(Optional.of(canteiro));

        Optional<Canteiro> resultado = service.buscarPorId("1");

        assertTrue(resultado.isPresent());
        assertEquals(canteiro, resultado.get());
    }

    @Test
    void buscarPorId_deveRetornarVazioQuandoNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertTrue(service.buscarPorId("99").isEmpty());
    }

    @Test
    void atualizar_deveAtualizarCamposQuandoExiste() {
        Canteiro dadosAtualizados = new Canteiro(
                null,
                "Canteiro 1 - Atualizado",
                "h2",
                "Rucula",
                "Joao",
                LocalDate.of(2026, 8, 5),
                LocalDate.of(2026, 9, 20),
                Status.COLHIDO
        );

        when(repository.findById("1")).thenReturn(Optional.of(canteiro));
        when(hortaRepository.existsById("h2")).thenReturn(true);
        when(repository.save(any(Canteiro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Canteiro resultado = service.atualizar("1", dadosAtualizados);

        assertEquals("Canteiro 1 - Atualizado", resultado.getNome());
        assertEquals("h2", resultado.getHortaId());
        assertEquals("Rucula", resultado.getCultivo());
        assertEquals("Joao", resultado.getResponsavel());
        assertEquals(Status.COLHIDO, resultado.getStatus());
        assertEquals("1", resultado.getId());
        verify(repository, times(1)).save(canteiro);
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoIdNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.atualizar("99", canteiro));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoHortaNaoExiste() {
        when(repository.findById("1")).thenReturn(Optional.of(canteiro));
        when(hortaRepository.existsById("h1")).thenReturn(false);

        assertThrows(ReferenciaInvalidaException.class, () -> service.atualizar("1", canteiro));
        verify(repository, never()).save(any());
    }

    @Test
    void remover_deveRemoverQuandoExiste() {
        when(repository.existsById("1")).thenReturn(true);
        when(escalaRepository.existsByCanteiroId("1")).thenReturn(false);

        assertDoesNotThrow(() -> service.remover("1"));

        verify(repository, times(1)).deleteById("1");
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.existsById("99")).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.remover("99"));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void remover_deveLancarExcecaoQuandoPossuiEscalas() {
        when(repository.existsById("1")).thenReturn(true);
        when(escalaRepository.existsByCanteiroId("1")).thenReturn(true);

        assertThrows(RecursoEmUsoException.class, () -> service.remover("1"));
        verify(repository, never()).deleteById(any());
    }
}
