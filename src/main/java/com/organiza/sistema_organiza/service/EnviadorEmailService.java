package com.organiza.sistema_organiza.service;

import org.springframework.stereotype.Service;

@Service
public class EnviadorEmailService {
    public void enviarCodigo(String email, String codigo) {
        System.out.println("ENVIANDO EMAIL PARA: " + email + " COM CÓDIGO: " + codigo);
    }
}
