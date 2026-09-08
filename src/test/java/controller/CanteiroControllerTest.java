package controller;

import com.cultivaplus.AEP_6S.Aep6SApplication;
import tools.jackson.databind.ObjectMapper;
import enums.Status;
import model.Canteiro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import repository.CanteiroRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Aep6SApplication.class)
@AutoConfigureMockMvc
class CanteiroControllerTest {

    @Configuration(proxyBeanMethods = false)
    static class ContainerConfig {
        @Bean
        @ServiceConnection
        MongoDBContainer mongoDbContainer() {
            return new MongoDBContainer(DockerImageName.parse("mongo:latest"));
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CanteiroRepository repository;

    private Canteiro canteiroExistente;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        canteiroExistente = repository.save(new Canteiro(
                null,
                "Canteiro 1",
                "Horta Comunitária Vila Verde",
                "Alface",
                "Maria",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 15),
                Status.EM_CULTIVO
        ));
    }

    @Test
    void cadastrar_deveCriarNovoCanteiro() throws Exception {
        Canteiro novo = new Canteiro(
                null,
                "Canteiro 2",
                "Horta Comunitária Vila Verde",
                "Tomate",
                "João",
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 10, 1),
                Status.EM_CULTIVO
        );

        mockMvc.perform(post("/canteiros")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Canteiro 2"))
                .andExpect(jsonPath("$.cultivo").value("Tomate"));
    }

    @Test
    void listarTodos_deveRetornarCanteirosCadastrados() throws Exception {
        mockMvc.perform(get("/canteiros"))
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
                "Horta Comunitária Vila Verde",
                "Rúcula",
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
        Canteiro atualizado = new Canteiro(
                null, "X", "X", "X", "X",
                LocalDate.now(), LocalDate.now(), Status.EM_CULTIVO
        );

        mockMvc.perform(put("/canteiros/idinexistente")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isNotFound());
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
}
