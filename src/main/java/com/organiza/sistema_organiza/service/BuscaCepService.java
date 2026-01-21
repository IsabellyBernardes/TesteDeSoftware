package com.organiza.sistema_organiza.service;

import org.springframework.stereotype.Service;

@Service
public class BuscaCepService {
    public String buscarPorCep(String cep) {
        // Simulação simples. Num teste real, você usaria Mockito para não depender da internet.
        if ("00000000".equals(cep)) return null; // Simula erro
        return "Rua das Flores, Bairro Centro"; // Retorno fixo para facilitar
    }
}
