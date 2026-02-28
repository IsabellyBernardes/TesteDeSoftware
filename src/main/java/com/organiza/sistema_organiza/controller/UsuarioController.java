package com.organiza.sistema_organiza.controller;

import com.organiza.sistema_organiza.model.Usuario;
import com.organiza.sistema_organiza.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        try {
            Usuario novo = service.cadastrarUsuario(usuario);
            return ResponseEntity.ok(novo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Inativação (TC_017, TC_018, TC_019)
    @PostMapping("/inativar")
    public ResponseEntity<?> inativar(@RequestBody Map<String, Object> payload) {
        try {
            String email = (String) payload.get("email");
            String codigo = (String) payload.get("codigo");
            int meses = (int) payload.get("meses");
            
            service.inativarConta(email, codigo, meses);
            return ResponseEntity.ok("Conta inativada temporariamente"); // Sucesso TC_017
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Erro de código 
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // Limite excedido 
        }
    }

    // Reativação via Senha (TC_019.2)
    @PutMapping("/reativar")
    public ResponseEntity<?> reativar(@RequestBody Map<String, String> payload) {
        try {
            service.redefinirSenhaEReativar(payload.get("email"), payload.get("novaSenha"));
            return ResponseEntity.ok("Senha alterada e conta reativada com sucesso"); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}