package service;

import enums.Status;
import model.Canteiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CanteiroRepository;
import service.CanteiroService;

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

    @InjectMocks
    private CanteiroService service;

    private Canteiro canteiro;

    @BeforeEach
    void setUp() {
        canteiro = new Canteiro(
                "1",
                "Canteiro 1",
                "Horta Comunitária Vila Verde",
                "Alface",
                "Maria",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 15),
                Status.EM_CULTIVO
        );
    }

    @Test
    void cadastrar_deveSalvarERetornarCanteiro() {
        when(repository.save(canteiro)).thenReturn(canteiro);

        Canteiro resultado = service.cadastrar(canteiro);

        assertEquals(canteiro, resultado);
        verify(repository, times(1)).save(canteiro);
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

        List<Canteiro> resultado = service.listarTodos();

        assertTrue(resultado.isEmpty());
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

        Optional<Canteiro> resultado = service.buscarPorId("99");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void atualizar_deveAtualizarCamposEExistente() {
        Canteiro dadosAtualizados = new Canteiro(
                null,
                "Canteiro 1 - Atualizado",
                "Horta Comunitária Vila Verde",
                "Rúcula",
                "João",
                LocalDate.of(2026, 8, 5),
                LocalDate.of(2026, 9, 20),
                Status.COLHIDO
        );

        when(repository.findById("1")).thenReturn(Optional.of(canteiro));
        when(repository.save(any(Canteiro.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Canteiro resultado = service.atualizar("1", dadosAtualizados);

        assertEquals("Canteiro 1 - Atualizado", resultado.getNome());
        assertEquals("Rúcula", resultado.getCultivo());
        assertEquals("João", resultado.getResponsavel());
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
    void remover_deveRemoverQuandoExiste() {
        when(repository.existsById("1")).thenReturn(true);

        assertDoesNotThrow(() -> service.remover("1"));

        verify(repository, times(1)).deleteById("1");
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.existsById("99")).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.remover("99"));
        verify(repository, never()).deleteById(any());
    }
}
