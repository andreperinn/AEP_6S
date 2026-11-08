package service;

import exception.RecursoEmUsoException;
import model.Horta;
import org.springframework.stereotype.Service;
import repository.CanteiroRepository;
import repository.HortaRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class HortaService {

    private final HortaRepository repository;
    private final CanteiroRepository canteiroRepository;

    public HortaService(HortaRepository repository, CanteiroRepository canteiroRepository) {
        this.repository = repository;
        this.canteiroRepository = canteiroRepository;
    }

    public Horta cadastrar(Horta horta) {
        return repository.save(horta);
    }

    public List<Horta> listarTodas() {
        return repository.findAll();
    }

    public Optional<Horta> buscarPorId(String id) {
        return repository.findById(id);
    }

    public Horta atualizar(String id, Horta dadosAtualizados) {
        Horta existente = repository.findById(id).orElseThrow();

        existente.setNome(dadosAtualizados.getNome());
        existente.setDescricao(dadosAtualizados.getDescricao());
        existente.setEndereco(dadosAtualizados.getEndereco());

        return repository.save(existente);
    }

    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException();
        }
        if (canteiroRepository.existsByHortaId(id)) {
            throw new RecursoEmUsoException("Horta possui canteiros cadastrados e nao pode ser removida");
        }
        repository.deleteById(id);
    }
}
