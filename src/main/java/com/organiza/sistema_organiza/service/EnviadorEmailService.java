package com.organiza.sistema_organiza.service;

import org.springframework.stereotype.Service;

@Service
public class EnviadorEmailService {
    public void enviarCodigo(String email, String codigo) {
        // Apenas imprime no console.
        // No teste, você verifica se este método foi chamado (verify).
        System.out.println("ENVIANDO EMAIL PARA: " + email + " COM CÓDIGO: " + codigo);
    }
}
