package com.organiza.sistema_organiza.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cnpj;
    private String tipo; // "ORGANIZADOR" ou "FORNECEDOR"
    private String cep;
    private String enderecoCompleto;
    private String numero;
    private String complemento;
    private String codigoVerificacao;
    private boolean ativo;
}
