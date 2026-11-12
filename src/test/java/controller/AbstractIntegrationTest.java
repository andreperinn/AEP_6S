package controller;

import com.cultivaplus.AEP_6S.Aep6SApplication;
import enums.Status;
import model.Canteiro;
import model.Endereco;
import model.Horta;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import repository.CanteiroRepository;
import repository.EscalaRepository;
import repository.HortaRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@SpringBootTest(classes = Aep6SApplication.class)
@AutoConfigureMockMvc
@Import(MongoContainerConfig.class)
abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected HortaRepository hortaRepository;

    @Autowired
    protected CanteiroRepository canteiroRepository;

    @Autowired
    protected EscalaRepository escalaRepository;

    @BeforeEach
    void limparBanco() {
        escalaRepository.deleteAll();
        canteiroRepository.deleteAll();
        hortaRepository.deleteAll();
    }

    protected Horta criarHorta() {
        return hortaRepository.save(new Horta(
                null,
                "Horta Comunitaria Vila Verde",
                "Horta mantida pelos moradores",
                new Endereco("Rua A", "Centro", "Maringa", "PR")
        ));
    }

    protected Canteiro criarCanteiro(String hortaId) {
        return canteiroRepository.save(new Canteiro(
                null,
                "Canteiro 1",
                hortaId,
                "Alface",
                "Maria",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 15),
                Status.EM_CULTIVO
        ));
    }
}
