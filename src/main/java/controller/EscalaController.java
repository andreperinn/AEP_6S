package controller;

import model.Escala;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.EscalaService;

import java.util.List;

@RestController
@RequestMapping("/escalas")
public class EscalaController {

    private final EscalaService service;

    public EscalaController(EscalaService service) {
        this.service = service;
    }

    @PostMapping
    public Escala cadastrar(@RequestBody Escala escala) {
        return service.cadastrar(escala);
    }

    @GetMapping
    public List<Escala> listar(@RequestParam(required = false) String canteiroId) {
        if (canteiroId != null) {
            return service.listarPorCanteiro(canteiroId);
        }
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Escala> buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Escala atualizar(@PathVariable String id, @RequestBody Escala dadosAtualizados) {
        return service.atualizar(id, dadosAtualizados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
