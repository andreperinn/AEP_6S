package model;

import enums.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "canteiros")
public class Canteiro {

    @Id
    private String id;
    private String nome;
    private String horta;
    private String cultivo;
    private String responsavel;
    private LocalDate dataPlantio;
    private LocalDate previsaoColheita;
    private Status status;

    public Canteiro(String id, String nome, String horta, String cultivo, String responsavel, LocalDate dataPlantio, LocalDate previsaoColheita, Status status) {
        this.id = id;
        this.nome = nome;
        this.horta = horta;
        this.cultivo = cultivo;
        this.responsavel = responsavel;
        this.dataPlantio = dataPlantio;
        this.previsaoColheita = previsaoColheita;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHorta() {
        return horta;
    }

    public void setHorta(String horta) {
        this.horta = horta;
    }

    public String getCultivo() {
        return cultivo;
    }

    public void setCultivo(String cultivo) {
        this.cultivo = cultivo;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public LocalDate getDataPlantio() {
        return dataPlantio;
    }

    public void setDataPlantio(LocalDate dataPlantio) {
        this.dataPlantio = dataPlantio;
    }

    public LocalDate getPrevisaoColheita() {
        return previsaoColheita;
    }

    public void setPrevisaoColheita(LocalDate previsaoColheita) {
        this.previsaoColheita = previsaoColheita;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
