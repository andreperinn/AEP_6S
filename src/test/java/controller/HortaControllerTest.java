package controller;

import model.Endereco;
import model.Horta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HortaControllerTest extends AbstractIntegrationTest {

    private Horta hortaExistente;

    @BeforeEach
    void setUp() {
        hortaExistente = criarHorta();
    }

    @Test
    void cadastrar_deveCriarHortaComEnderecoAninhado() throws Exception {
        Horta nova = new Horta(null, "Horta da Escola", "Horta escolar",
                new Endereco("Rua B", "Zona 7", "Maringa", "PR"));

        mockMvc.perform(post("/hortas")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(nova)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Horta da Escola"))
                .andExpect(jsonPath("$.endereco.rua").value("Rua B"))
                .andExpect(jsonPath("$.endereco.cidade").value("Maringa"));
    }

    @Test
    void listarTodas_deveRetornarHortasCadastradas() throws Exception {
        mockMvc.perform(get("/hortas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].endereco.estado").value("PR"));
    }

    @Test
    void buscarPorId_deveRetornarHortaQuandoExiste() throws Exception {
        mockMvc.perform(get("/hortas/" + hortaExistente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Horta Comunitaria Vila Verde"));
    }

    @Test
    void buscarPorId_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(get("/hortas/idinexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizar_deveAtualizarHortaExistente() throws Exception {
        Horta atualizada = new Horta(null, "Horta Renovada", "Nova descricao",
                new Endereco("Rua C", "Centro", "Sarandi", "PR"));

        mockMvc.perform(put("/hortas/" + hortaExistente.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Horta Renovada"))
                .andExpect(jsonPath("$.endereco.cidade").value("Sarandi"));
    }

    @Test
    void atualizar_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(put("/hortas/idinexistente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(hortaExistente)))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_deveRemoverHortaSemCanteiros() throws Exception {
        mockMvc.perform(delete("/hortas/" + hortaExistente.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/hortas/" + hortaExistente.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_deveRetornar404QuandoNaoExiste() throws Exception {
        mockMvc.perform(delete("/hortas/idinexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void remover_deveRetornar409QuandoHortaPossuiCanteiros() throws Exception {
        criarCanteiro(hortaExistente.getId());

        mockMvc.perform(delete("/hortas/" + hortaExistente.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").exists());
    }
}
