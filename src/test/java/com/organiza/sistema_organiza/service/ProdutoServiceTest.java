package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Produto;
import com.organiza.sistema_organiza.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    // --- TC_020: Validar o estado inicial da busca (Listar catálogo inteiro) ---
    @Test
    void tc020_deveListarTodosOsProdutosSemFiltros() {
        // Cenário: Clicar em buscar com campos vazios 
        when(produtoRepository.findAll()).thenReturn(Arrays.asList(new Produto(), new Produto()));

        List<Produto> resultado = produtoService.listarTodos();

        assertEquals(2, resultado.size()); 
        verify(produtoRepository).findAll();
    }

    // --- TC_021: Validar refinamento com múltiplos critérios (Nome e Tipo) ---
    @Test
    void tc021_deveFiltrarPorNomeETipo() {
        // Cenário: Nome "Mesa" e Tipo "Escritório"
        Produto mesa = new Produto();
        mesa.setNome("Mesa de Madeira");
        mesa.setTipo("Escritório");

        when(produtoRepository.findByNomeContainingIgnoreCaseAndTipo("Mesa", "Escritório"))
            .thenReturn(Arrays.asList(mesa));

        List<Produto> resultado = produtoService.buscarProdutos("Mesa", "Escritório");

        assertFalse(resultado.isEmpty());
        assertEquals("Escritório", resultado.get(0).getTipo()); 
    }

    // --- TC_022: Validar busca com filtro de Status ---
    @Test
    void tc022_deveFiltrarApenasProdutosComStatusAtivo() {
        // Cenário: Selecionar status "Ativo"
        when(produtoRepository.findByStatus("Ativo")).thenReturn(Arrays.asList(new Produto()));

        List<Produto> resultado = produtoService.buscarApenasAtivos();

        assertFalse(resultado.isEmpty());
        verify(produtoRepository).findByStatus("Ativo"); 
    }

    // --- TC_026: Validar resposta contra entradas inválidas (Sanitização) ---
    @Test
    void tc026_deveLancarExcecaoParaCaracteresInvalidos() {
        // Cenário: Inserir "%%" ou "@@" no nome 
        String termoInvalido = "%% OR @@";

        Exception e = assertThrows(IllegalArgumentException.class, () -> 
            produtoService.buscaSegura(termoInvalido));

        assertEquals("Erro: entrada inválida no campo de busca.", e.getMessage()); 
    }

    // --- TC_029: Validar busca parcial (Lógica "Contém") ---
    @Test
    void tc029_deveEncontrarProdutoComNomeParcial() {
        // Cenário: Digitar apenas "Ergonômica" para encontrar "Cadeira de Escritório Ergonômica" 
        Produto cadeira = new Produto();
        cadeira.setNome("Cadeira de Escritório Ergonômica");

        when(produtoRepository.findByNomeContainingIgnoreCase("Ergonômica"))
            .thenReturn(Arrays.asList(cadeira));

        List<Produto> resultado = produtoService.buscarProdutos("Ergonômica", null);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).getNome().contains("Ergonômica")); 
    }
}