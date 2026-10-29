package model;

import enums.TipoCuidado;

import java.time.LocalDate;

public class Tarefa {

    private TipoCuidado tipo;
    private LocalDate data;
    private boolean concluida;
    private String observacao;

    public Tarefa(TipoCuidado tipo, LocalDate data, boolean concluida, String observacao) {
        this.tipo = tipo;
        this.data = data;
        this.concluida = concluida;
        this.observacao = observacao;
    }

    public TipoCuidado getTipo() {
        return tipo;
    }

    public void setTipo(TipoCuidado tipo) {
        this.tipo = tipo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
