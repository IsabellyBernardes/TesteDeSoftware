package com.organiza.sistema_organiza.controller;

import com.organiza.sistema_organiza.model.Produto;
import com.organiza.sistema_organiza.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // Busca geral e refinada (TC_020, TC_021, TC_029)
    @GetMapping("/busca")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo) {
        try {
            // Se for busca segura (TC_026)
            if (nome != null && (nome.contains("%") || nome.contains("@"))) {
                return ResponseEntity.badRequest().body("Erro: entrada inválida no campo de busca."); // 
            }
            
            List<Produto> produtos = produtoService.buscarProdutos(nome, tipo);
            return ResponseEntity.ok(produtos); // Lista de produtos TC_020
        } catch (Exception e) {
            // Simulação de erro sistêmico (TC_027)
            return ResponseEntity.status(500).body("Não foi possível carregar os produtos."); 
        }
    }

    // Filtro por status (TC_022)
    @GetMapping("/ativos")
    public List<Produto> listarAtivos() {
        return produtoService.buscarApenasAtivos(); // Retorna apenas status "ativo"
    }
}