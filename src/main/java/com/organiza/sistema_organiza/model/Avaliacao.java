package com.organiza.sistema_organiza.model;

import jakarta.persistence.*;

@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer estrelas;
    private String comentario;

    @ManyToOne
    private Usuario fornecedor;

    @ManyToOne
    private Evento evento;

    public Avaliacao() {}

    public Avaliacao(Integer estrelas, String comentario, Usuario fornecedor, Evento evento) {
        this.estrelas = estrelas;
        this.comentario = comentario;
        this.fornecedor = fornecedor;
        this.evento = evento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEstrelas() {
        return estrelas;
    }

    public void setEstrelas(Integer estrelas) {
        this.estrelas = estrelas;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Usuario getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Usuario fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}