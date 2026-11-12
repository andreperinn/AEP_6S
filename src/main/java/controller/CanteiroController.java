package controller;

import model.Canteiro;
import service.CanteiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/canteiros")
public class CanteiroController {

    private final CanteiroService service;

    public CanteiroController(CanteiroService service) {
        this.service = service;
    }

    @PostMapping
    public Canteiro cadastrar(@RequestBody Canteiro canteiro) {
        return service.cadastrar(canteiro);
    }

    @GetMapping
    public List<Canteiro> listar(@RequestParam(required = false) String hortaId) {
        if (hortaId != null) {
            return service.listarPorHorta(hortaId);
        }
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Canteiro> buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Canteiro atualizar(@PathVariable String id, @RequestBody Canteiro dadosAtualizados) {
        return service.atualizar(id, dadosAtualizados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
