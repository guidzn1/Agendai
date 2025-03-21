package com.example.agendai.models;

import java.io.Serializable;

public class Evento implements Serializable {
    private int id;
    private String titulo;
    private String contato;
    private String data;         // Formato "dd/MM/yyyy"
    private String horaInicio;   // Formato "HH:mm"
    private String horaTermino;  // Formato "HH:mm"
    private String local;        // Ex.: "Campo Sintético 1", etc.
    private String pagamento;    // "50%" ou "100%"
    private double valorAluguel; // Valor do aluguel (ex.: 120.00 ou 90.00)
    private boolean concluido;   // true se concluído
    private String status;       // Ex.: "Agendado", "Cancelado", "Reagendado"

    // Construtor sem ID para criação – define status padrão "Agendado"
    public Evento(String titulo, String contato, String data, String horaInicio, String horaTermino,
                  String local, String pagamento, double valorAluguel) {
        this.titulo = titulo;
        this.contato = contato;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
        this.local = local;
        this.pagamento = pagamento;
        this.valorAluguel = valorAluguel;
        this.concluido = false;
        this.status = "Agendado";
    }

    // Construtor completo (caso você precise definir tudo, inclusive o id e o status)
    public Evento(int id, String titulo, String contato, String data, String horaInicio, String horaTermino,
                  String local, String pagamento, double valorAluguel, boolean concluido, String status) {
        this.id = id;
        this.titulo = titulo;
        this.contato = contato;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
        this.local = local;
        this.pagamento = pagamento;
        this.valorAluguel = valorAluguel;
        this.concluido = concluido;
        this.status = status;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getContato() {
        return contato;
    }
    public void setContato(String contato) {
        this.contato = contato;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }
    public String getHoraTermino() {
        return horaTermino;
    }
    public void setHoraTermino(String horaTermino) {
        this.horaTermino = horaTermino;
    }
    public String getLocal() {
        return local;
    }
    public void setLocal(String local) {
        this.local = local;
    }
    public String getPagamento() {
        return pagamento;
    }
    public void setPagamento(String pagamento) {
        this.pagamento = pagamento;
    }
    public double getValorAluguel() {
        return valorAluguel;
    }
    public void setValorAluguel(double valorAluguel) {
        this.valorAluguel = valorAluguel;
    }
    public boolean isConcluido() {
        return concluido;
    }
    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
