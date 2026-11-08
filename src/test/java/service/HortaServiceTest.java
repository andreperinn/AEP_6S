package service;

import exception.RecursoEmUsoException;
import model.Endereco;
import model.Horta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CanteiroRepository;
import repository.HortaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HortaServiceTest {

    @Mock
    private HortaRepository repository;

    @Mock
    private CanteiroRepository canteiroRepository;

    @InjectMocks
    private HortaService service;

    private Horta horta;

    @BeforeEach
    void setUp() {
        horta = new Horta("h1", "Horta Vila Verde", "Horta comunitaria",
                new Endereco("Rua A", "Centro", "Maringa", "PR"));
    }

    @Test
    void cadastrar_deveSalvarERetornarHorta() {
        when(repository.save(horta)).thenReturn(horta);

        assertEquals(horta, service.cadastrar(horta));
        verify(repository, times(1)).save(horta);
    }

    @Test
    void listarTodas_deveRetornarHortas() {
        when(repository.findAll()).thenReturn(List.of(horta));

        List<Horta> resultado = service.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Maringa", resultado.get(0).getEndereco().getCidade());
    }

    @Test
    void buscarPorId_deveRetornarHortaQuandoExiste() {
        when(repository.findById("h1")).thenReturn(Optional.of(horta));

        assertEquals(horta, service.buscarPorId("h1").orElseThrow());
    }

    @Test
    void buscarPorId_deveRetornarVazioQuandoNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertTrue(service.buscarPorId("99").isEmpty());
    }

    @Test
    void atualizar_deveAtualizarCamposQuandoExiste() {
        Horta dados = new Horta(null, "Horta Renovada", "Nova descricao",
                new Endereco("Rua B", "Zona 7", "Sarandi", "PR"));
        when(repository.findById("h1")).thenReturn(Optional.of(horta));
        when(repository.save(any(Horta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Horta resultado = service.atualizar("h1", dados);

        assertEquals("Horta Renovada", resultado.getNome());
        assertEquals("Nova descricao", resultado.getDescricao());
        assertEquals("Sarandi", resultado.getEndereco().getCidade());
        assertEquals("h1", resultado.getId());
    }

    @Test
    void atualizar_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.atualizar("99", horta));
        verify(repository, never()).save(any());
    }

    @Test
    void remover_deveRemoverQuandoNaoPossuiCanteiros() {
        when(repository.existsById("h1")).thenReturn(true);
        when(canteiroRepository.existsByHortaId("h1")).thenReturn(false);

        assertDoesNotThrow(() -> service.remover("h1"));
        verify(repository, times(1)).deleteById("h1");
    }

    @Test
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        when(repository.existsById("99")).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.remover("99"));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void remover_deveLancarExcecaoQuandoPossuiCanteiros() {
        when(repository.existsById("h1")).thenReturn(true);
        when(canteiroRepository.existsByHortaId("h1")).thenReturn(true);

        assertThrows(RecursoEmUsoException.class, () -> service.remover("h1"));
        verify(repository, never()).deleteById(any());
    }
}
