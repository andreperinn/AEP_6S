package service;

import exception.RecursoEmUsoException;
import exception.ReferenciaInvalidaException;
import model.Canteiro;
import org.springframework.stereotype.Service;
import repository.CanteiroRepository;
import repository.EscalaRepository;
import repository.HortaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CanteiroService {

    private final CanteiroRepository repository;
    private final HortaRepository hortaRepository;
    private final EscalaRepository escalaRepository;

    public CanteiroService(CanteiroRepository repository, HortaRepository hortaRepository, EscalaRepository escalaRepository) {
        this.repository = repository;
        this.hortaRepository = hortaRepository;
        this.escalaRepository = escalaRepository;
    }

    public Canteiro cadastrar(Canteiro canteiro) {
        validarHorta(canteiro.getHortaId());
        return repository.save(canteiro);
    }

    public List<Canteiro> listarTodos() {
        return repository.findAll();
    }

    public List<Canteiro> listarPorHorta(String hortaId) {
        return repository.findByHortaId(hortaId);
    }

    public Optional<Canteiro> buscarPorId(String id) {
        return repository.findById(id);
    }

    public Canteiro atualizar(String id, Canteiro dadosAtualizados) {
        Canteiro existente = repository.findById(id).orElseThrow();
        validarHorta(dadosAtualizados.getHortaId());

        existente.setNome(dadosAtualizados.getNome());
        existente.setHortaId(dadosAtualizados.getHortaId());
        existente.setCultivo(dadosAtualizados.getCultivo());
        existente.setResponsavel(dadosAtualizados.getResponsavel());
        existente.setDataPlantio(dadosAtualizados.getDataPlantio());
        existente.setPrevisaoColheita(dadosAtualizados.getPrevisaoColheita());
        existente.setStatus(dadosAtualizados.getStatus());

        return repository.save(existente);
    }

    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException();
        }
        if (escalaRepository.existsByCanteiroId(id)) {
            throw new RecursoEmUsoException("Canteiro possui escalas cadastradas e nao pode ser removido");
        }
        repository.deleteById(id);
    }

    private void validarHorta(String hortaId) {
        if (hortaId == null || hortaId.isBlank() || !hortaRepository.existsById(hortaId)) {
            throw new ReferenciaInvalidaException("Horta informada nao existe: " + hortaId);
        }
    }
}
