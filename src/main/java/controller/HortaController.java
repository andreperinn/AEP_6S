package controller;

import model.Horta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.HortaService;

import java.util.List;

@RestController
@RequestMapping("/hortas")
public class HortaController {

    private final HortaService service;

    public HortaController(HortaService service) {
        this.service = service;
    }

    @PostMapping
    public Horta cadastrar(@RequestBody Horta horta) {
        return service.cadastrar(horta);
    }

    @GetMapping
    public List<Horta> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Horta> buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Horta atualizar(@PathVariable String id, @RequestBody Horta dadosAtualizados) {
        return service.atualizar(id, dadosAtualizados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
