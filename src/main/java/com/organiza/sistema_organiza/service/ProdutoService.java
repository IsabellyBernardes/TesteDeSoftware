package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Produto;
import com.organiza.sistema_organiza.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // TC_020: Listar tudo [cite: 110]
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    // TC_021 e TC_029: Busca por nome (contém) e tipo [cite: 124, 230]
    public List<Produto> buscarProdutos(String nome, String tipo) {
        if (nome != null && tipo != null) {
            return produtoRepository.findByNomeContainingIgnoreCaseAndTipo(nome, tipo);
        } else if (nome != null) {
            return produtoRepository.findByNomeContainingIgnoreCase(nome);
        }
        return listarTodos();
    }

    // TC_022: Filtrar apenas ativos 
    public List<Produto> buscarApenasAtivos() {
        return produtoRepository.findByStatus("Ativo");
    }

    // TC_026: Sanitização de entrada [cite: 197]
    public List<Produto> buscaSegura(String termo) {
        if (termo.contains("%") || termo.contains("@")) {
            throw new IllegalArgumentException("Erro: entrada inválida no campo de busca.");
        }
        return produtoRepository.findByNomeContainingIgnoreCase(termo);
    }
}