package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "escalas")
public class Escala {

    @Id
    private String id;
    private String canteiroId;
    private String responsavel;
    private List<Tarefa> tarefas;

    public Escala(String id, String canteiroId, String responsavel, List<Tarefa> tarefas) {
        this.id = id;
        this.canteiroId = canteiroId;
        this.responsavel = responsavel;
        this.tarefas = tarefas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCanteiroId() {
        return canteiroId;
    }

    public void setCanteiroId(String canteiroId) {
        this.canteiroId = canteiroId;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }
}
