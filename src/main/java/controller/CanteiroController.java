package controller;

import model.Canteiro;
import service.CanteiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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
    public List<Canteiro> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Canteiro> buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Canteiro> atualizar(@PathVariable String id, @RequestBody Canteiro dadosAtualizados) {
        try {
            return ResponseEntity.ok(service.atualizar(id, dadosAtualizados));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        try {
            service.remover(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}