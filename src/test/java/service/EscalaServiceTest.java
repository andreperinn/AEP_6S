package service;

import enums.TipoCuidado;
import exception.ReferenciaInvalidaException;
import model.Escala;
import model.Tarefa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CanteiroRepository;
import repository.EscalaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalaServiceTest {

    @Mock
    private EscalaRepository repository;

    @Mock
    private CanteiroRepository canteiroRepository;

    @InjectMocks
    private EscalaService service;

    private Escala escala;

    @BeforeEach
    void setUp() {
        escala = new Escala("e1", "c1", "Maria", List.of(
                new Tarefa(TipoCuidado.REGA, LocalDate.of(2026, 9, 10), false, "Regar de manha")
        ));
    }

    @Test
    void cadastrar_deveSalvarQuandoCanteiroExiste() {
        when(canteiroRepository.existsById("c1")).thenReturn(true);
        when(repository.save(escala)).thenReturn(escala);

        assertEquals(escala, service.cadastrar(escala));
        verify(repository, times(1)).save(escala);
    }

    @Test
    void cadastrar_deveLancarExcecaoQuandoCanteiroNaoExiste() {
        when(canteiroRepository.existsById("c1")).thenReturn(false);

        assertThrows(ReferenciaInvalidaException.class, () -> service.cadastrar(escala));
        verify(repository, never()).save(any());
    }

    @Test
    void cadastrar_deveLancarExcecaoQuandoCanteiroNaoInformado() {
        escala.setCanteiroId(" ");

        assertThrows(ReferenciaInvalidaException.class, () -> service.cadastrar(escala));
        verify(repository, never()).save(any());
    }

    @Test
    void listarTodas_deveRetornarEscalas() {
        when(repository.findAll()).thenReturn(List.of(escala));

        assertEquals(1, service.listarTodas().size());
    }

    @Test
    void listarPorCanteiro_deveRetornarEscalasDoCanteiro() {
        when(repository.findByCanteiroId("c1")).thenReturn(List.of(escala));

        List<Escala> resultado = service.listarPorCanteiro("c1");

        assertEquals(1, resultado.size());
        assertEquals(TipoCuidado.REGA, resultado.get(0).getTarefas().get(0).getTipo());
    }

    @Test
    void buscarPorId_deveRetornarEscalaQuandoExiste() {
        when(repository.findById("e1")).thenReturn(Optional.of(escala));

        assertEquals(escala, service.buscarPorId("e1").orElseThrow());
    }

    @Test
    void buscarPorId_deveRetornarVazioQuandoNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertTrue(service.buscarPorId("99").isEmpty());
    }

    @Test
    void atualizar_deveAtualizarCamposQuandoExiste() {
        Escala dados = new Escala(null, "c2", "Pedro", List.of(
                new Tarefa(TipoCuidado.PODA, LocalDate.of(2026, 9, 20), true, "Feito")
        ));
        when(repository.findById("e1")).thenReturn(Optional.of(escala));
        when(canteiroRepository.existsById("c2")).thenReturn(true);
        when(repository.save(any(Escala.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Escala resultado = service.atualizar("e1", dados);

        assertEquals("c2", resultado.getCanteiroId());
        assertEquals("Pedro", resultado.getResponsavel());
        assertTrue(resultado.getTarefas().get(0).isConcluida());
        assertEquals("e1", resultado.getId());
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.atualizar("99", escala));
        verify(repository, never()).save(any());
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoCanteiroNaoExiste() {
        when(repository.findById("e1")).thenReturn(Optional.of(escala));
        when(canteiroRepository.existsById("c1")).thenReturn(false);

        assertThrows(ReferenciaInvalidaException.class, () -> service.atualizar("e1", escala));
        verify(repository, never()).save(any());
    }

    @Test
    void remover_deveRemoverQuandoExiste() {
        when(repository.existsById("e1")).thenReturn(true);

        assertDoesNotThrow(() -> service.remover("e1"));
        verify(repository, times(1)).deleteById("e1");
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.existsById("99")).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.remover("99"));
        verify(repository, never()).deleteById(any());
    }
}
