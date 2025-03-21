package com.example.agendai.models;

import java.io.Serializable;

public class Espaco implements Serializable {
    private String nome;
    private double valorPorHora;

    public Espaco(String nome, double valorPorHora) {
        this.nome = nome;
        this.valorPorHora = valorPorHora;
    }

    public String getNome() {
        return nome;
    }

    public double getValorPorHora() {
        return valorPorHora;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setValorPorHora(double valorPorHora) {
        this.valorPorHora = valorPorHora;
    }

    @Override
    public String toString() {
        return nome;
    }
}
