package service;

import exception.ReferenciaInvalidaException;
import model.Escala;
import org.springframework.stereotype.Service;
import repository.CanteiroRepository;
import repository.EscalaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EscalaService {

    private final EscalaRepository repository;
    private final CanteiroRepository canteiroRepository;

    public EscalaService(EscalaRepository repository, CanteiroRepository canteiroRepository) {
        this.repository = repository;
        this.canteiroRepository = canteiroRepository;
    }

    public Escala cadastrar(Escala escala) {
        validarCanteiro(escala.getCanteiroId());
        return repository.save(escala);
    }

    public List<Escala> listarTodas() {
        return repository.findAll();
    }

    public List<Escala> listarPorCanteiro(String canteiroId) {
        return repository.findByCanteiroId(canteiroId);
    }

    public Optional<Escala> buscarPorId(String id) {
        return repository.findById(id);
    }

    public Escala atualizar(String id, Escala dadosAtualizados) {
        Escala existente = repository.findById(id).orElseThrow();
        validarCanteiro(dadosAtualizados.getCanteiroId());

        existente.setCanteiroId(dadosAtualizados.getCanteiroId());
        existente.setResponsavel(dadosAtualizados.getResponsavel());
        existente.setTarefas(dadosAtualizados.getTarefas());

        return repository.save(existente);
    }

    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException();
        }
        repository.deleteById(id);
    }

    private void validarCanteiro(String canteiroId) {
        if (canteiroId == null || canteiroId.isBlank() || !canteiroRepository.existsById(canteiroId)) {
            throw new ReferenciaInvalidaException("Canteiro informado nao existe: " + canteiroId);
        }
    }
}
