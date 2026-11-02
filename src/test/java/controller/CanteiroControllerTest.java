package controller;

import enums.Status;
import model.Canteiro;
import model.Escala;
import model.Horta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CanteiroControllerTest extends AbstractIntegrationTest {

    private Horta horta;
    private Canteiro canteiroExistente;

    @BeforeEach
    void setUp() {
        horta = criarHorta();
        canteiroExistente = criarCanteiro(horta.getId());
    }

    private Canteiro novoCanteiro(String hortaId) {
        return new Canteiro(
                null,
                "Canteiro 2",
                hortaId,
                "Tomate",
                "Joao",
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 10, 1),
                Status.EM_CULTIVO
        );
    }

    @Test
    void cadastrar_deveCriarNovoCanteiro() throws Exception {
        mockMvc.perform(post("/canteiros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoCanteiro(horta.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Canteiro 2"))
                .andExpect(jsonPath("$.hortaId").value(horta.getId()))
                .andExpect(jsonPath("$.cultivo").value("Tomate"));
    }

    @Test
    void cadastrar_deveRetornar400QuandoHortaNaoExiste() throws Exception {
        mockMvc.perform(post("/canteiros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoCanteiro("hortainexistente"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void listarTodos_deveRetornarCanteirosCadastrados() throws Exception {
        mockMvc.perform(get("/canteiros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Canteiro 1"));
    }

    @Test
    void listar_deveFiltrarCanteirosPorHorta() throws Exception {
        Horta outraHorta = hortaRepository.save(new Horta(null, "Outra Horta", null, null));
        canteiroRepository.save(novoCanteiro(outraHorta.getId()));

        mockMvc.perform(get("/canteiros").param("hortaId", horta.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Canteiro 1"));
    }

    @Test
    void buscarPorId_deveRetornarCanteiroQuandoExiste() throws Exception {
        mockMvc.perform(get("/canteiros/" + canteiroExistente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Canteiro 1"));
    }

    @Test
    void buscarPorId_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(get("/canteiros/idinexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizar_deveAtualizarCanteiroExistente() throws Exception {
        Canteiro atualizado = new Canteiro(
                null,
                "Canteiro 1 - Atualizado",
                horta.getId(),
                "Rucula",
                "Pedro",
                LocalDate.of(2026, 8, 2),
                LocalDate.of(2026, 9, 20),
                Status.COLHIDO
        );

        mockMvc.perform(put("/canteiros/" + canteiroExistente.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Canteiro 1 - Atualizado"))
                .andExpect(jsonPath("$.status").value("COLHIDO"));
    }

    @Test
    void atualizar_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(put("/canteiros/idinexistente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoCanteiro(horta.getId()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizar_deveRetornar400QuandoHortaNaoExiste() throws Exception {
        mockMvc.perform(put("/canteiros/" + canteiroExistente.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoCanteiro("hortainexistente"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void remover_deveRemoverCanteiroExistente() throws Exception {
        mockMvc.perform(delete("/canteiros/" + canteiroExistente.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/canteiros/" + canteiroExistente.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(delete("/canteiros/idinexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_deveRetornar409QuandoCanteiroPossuiEscala() throws Exception {
        escalaRepository.save(new Escala(null, canteiroExistente.getId(), "Maria", List.of()));

        mockMvc.perform(delete("/canteiros/" + canteiroExistente.getId()))
                .andExpect(status().isConflict());
    }
}
