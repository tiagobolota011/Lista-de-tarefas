package com.example.listadetarefas;
public class Tarefa {
    private int id;
    private String titulo;
    private String descricao;
    private long data; // Em timestamp (epoch)
    private int hora;
    private int prioridade; // 0: Baixa, 1: Média, 2: Alta
    private String categoria; // "Pessoal", "Trabalho", "Estudos"
    private int lembrete; // minutos antes
    private boolean repetir; // ou String para frequência

    public Tarefa(int id, String titulo, String descricao, long data, int hora, int prioridade, String categoria, int lembrete, boolean repetir) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.hora = hora;
        this.prioridade = prioridade;
        this.categoria = categoria;
        this.lembrete = lembrete;
        this.repetir = repetir;
    }

    // Getters e setters aqui
}

