package com.organiza.sistema_organiza.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    private String descricao;
    private String tipo; 
    private String status; 
    
    @ManyToOne
    private Usuario fornecedor; 

    public Produto() {}

    public Produto(String nome, String tipo, String status, Usuario fornecedor) {
        this.nome = nome;
        this.tipo = tipo;
        this.status = status;
        this.fornecedor = fornecedor;
    }
}