package com.organiza.sistema_organiza.repository;

import com.organiza.sistema_organiza.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Busca por contém (TC_029)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca com múltiplos filtros (TC_021)
    List<Produto> findByNomeContainingIgnoreCaseAndTipo(String nome, String tipo);

    // Busca por Status (TC_022)
    List<Produto> findByStatus(String status);
}