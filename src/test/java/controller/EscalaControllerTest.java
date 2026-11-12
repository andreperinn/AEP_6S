package controller;

import enums.TipoCuidado;
import model.Canteiro;
import model.Escala;
import model.Tarefa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EscalaControllerTest extends AbstractIntegrationTest {

    private Canteiro canteiro;
    private Escala escalaExistente;

    @BeforeEach
    void setUp() {
        canteiro = criarCanteiro(criarHorta().getId());
        escalaExistente = escalaRepository.save(novaEscala(canteiro.getId()));
    }

    private Escala novaEscala(String canteiroId) {
        return new Escala(null, canteiroId, "Maria", List.of(
                new Tarefa(TipoCuidado.REGA, LocalDate.of(2026, 9, 10), false, "Regar de manha"),
                new Tarefa(TipoCuidado.ADUBACAO, LocalDate.of(2026, 9, 12), false, null)
        ));
    }

    @Test
    void cadastrar_deveCriarEscalaComListaDeTarefas() throws Exception {
        mockMvc.perform(post("/escalas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novaEscala(canteiro.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.canteiroId").value(canteiro.getId()))
                .andExpect(jsonPath("$.tarefas", hasSize(2)))
                .andExpect(jsonPath("$.tarefas[0].tipo").value("REGA"))
                .andExpect(jsonPath("$.tarefas[0].concluida").value(false));
    }

    @Test
    void cadastrar_deveRetornar400QuandoCanteiroNaoExiste() throws Exception {
        mockMvc.perform(post("/escalas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novaEscala("canteiroinexistente"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void listar_deveRetornarTodasAsEscalas() throws Exception {
        mockMvc.perform(get("/escalas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tarefas[1].tipo").value("ADUBACAO"));
    }

    @Test
    void listar_deveFiltrarEscalasPorCanteiro() throws Exception {
        Canteiro outro = criarCanteiro(canteiro.getHortaId());
        escalaRepository.save(novaEscala(outro.getId()));

        mockMvc.perform(get("/escalas").param("canteiroId", canteiro.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].canteiroId").value(canteiro.getId()));
    }

    @Test
    void buscarPorId_deveRetornarEscalaQuandoExiste() throws Exception {
        mockMvc.perform(get("/escalas/" + escalaExistente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responsavel").value("Maria"));
    }

    @Test
    void buscarPorId_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(get("/escalas/idinexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizar_deveAtualizarEscalaEMarcarTarefaComoConcluida() throws Exception {
        Escala atualizada = new Escala(null, canteiro.getId(), "Pedro", List.of(
                new Tarefa(TipoCuidado.REGA, LocalDate.of(2026, 9, 10), true, "Feito")
        ));

        mockMvc.perform(put("/escalas/" + escalaExistente.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responsavel").value("Pedro"))
                .andExpect(jsonPath("$.tarefas", hasSize(1)))
                .andExpect(jsonPath("$.tarefas[0].concluida").value(true));
    }

    @Test
    void atualizar_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(put("/escalas/idinexistente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novaEscala(canteiro.getId()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizar_deveRetornar400QuandoCanteiroNaoExiste() throws Exception {
        mockMvc.perform(put("/escalas/" + escalaExistente.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novaEscala("canteiroinexistente"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void remover_deveRemoverEscalaExistente() throws Exception {
        mockMvc.perform(delete("/escalas/" + escalaExistente.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/escalas/" + escalaExistente.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(delete("/escalas/idinexistente"))
                .andExpect(status().isNotFound());
    }
}
