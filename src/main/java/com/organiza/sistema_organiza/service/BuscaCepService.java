package com.organiza.sistema_organiza.service;

import org.springframework.stereotype.Service;

@Service
public class BuscaCepService {
    public String buscarPorCep(String cep) {
        if ("00000000".equals(cep)) return null;
        return "Rua das Flores, Bairro Centro";
    }
}
