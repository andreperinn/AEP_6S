package service;

import model.Canteiro;
import org.springframework.stereotype.Service;
import repository.CanteiroRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CanteiroService {

    private final CanteiroRepository repository;

    public CanteiroService(CanteiroRepository repository) {
        this.repository = repository;
    }

    public Canteiro cadastrar (Canteiro canteiro){
        return repository.save(canteiro);
    }

    public List<Canteiro> listarTodos(){
        return repository.findAll();
    }

    public Optional<Canteiro> buscarPorId(String id) {
        return repository.findById(id);
    }

    public Canteiro atualizar(String id, Canteiro dadosAtualizados) {
        Canteiro existente = repository.findById(id).orElseThrow();

        existente.setNome(dadosAtualizados.getNome());
        existente.setHorta(dadosAtualizados.getHorta());
        existente.setCultivo(dadosAtualizados.getCultivo());
        existente.setResponsavel(dadosAtualizados.getResponsavel());
        existente.setDataPlantio(dadosAtualizados.getDataPlantio());
        existente.setPrevisaoColheita(dadosAtualizados.getPrevisaoColheita());
        existente.setStatus(dadosAtualizados.getStatus());

        return repository.save(existente);
    }

    public void remover(String id){
        if(!repository.existsById(id)){
            throw new NoSuchElementException();
        }
        repository.deleteById(id);
    }

}