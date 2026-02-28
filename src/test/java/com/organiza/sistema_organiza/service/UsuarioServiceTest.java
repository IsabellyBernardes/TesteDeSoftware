package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Usuario;
import com.organiza.sistema_organiza.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private EnviadorEmailService emailService;

    // Método auxiliar para criar um fornecedor padrão para os testes
    private Usuario criarFornecedorPadrao() {
        Usuario u = new Usuario();
        u.setNome("Fornecedor Teste");
        u.setEmail("fornecedor@teste.com");
        u.setSenha("Senha123!");
        u.setTipo("FORNECEDOR");
        u.setAtivo(true);
        u.setCodigoVerificacao("123456"); 
        return u;
    }

    // --- TC_017: Validar se o fornecedor consegue inativar sua conta ---
    @Test
    void tc017_deveInativarFornecedorComSucesso() {
        Usuario usuario = criarFornecedorPadrao();
        String email = usuario.getEmail();
        String codigoCorreto = "123456";

        when(repository.findByEmail(email)).thenReturn(usuario);

        // Ação: Inativar por 6 meses
        service.inativarConta(email, codigoCorreto, 6);

        assertFalse(usuario.isAtivo()); 
        verify(repository).save(usuario);
    }

    // --- TC_018 e TC_019: Inativar com código incorreto e limite de tentativas ---
    @Test
    void tc019_deveBloquearInativacaoAposLimiteDeTentativasExcedido() {
        Usuario usuario = criarFornecedorPadrao();
        String email = usuario.getEmail();
        String codigoErrado = "000000";

        when(repository.findByEmail(email)).thenReturn(usuario);

        // Simula as tentativas falhas
        assertThrows(IllegalArgumentException.class, () -> service.inativarConta(email, codigoErrado, 6)); 
        assertThrows(IllegalArgumentException.class, () -> service.inativarConta(email, codigoErrado, 6)); 

        // Na 3ª tentativa, o sistema deve suspender o processo
        Exception e = assertThrows(IllegalStateException.class, () -> service.inativarConta(email, codigoErrado, 6));
        
        assertEquals("Limite de tentativas excedido. O processo de inativação foi suspenso.", e.getMessage());
        assertTrue(usuario.isAtivo()); 
    }

    // --- TC_019.2: Validar reativação automática via redefinição de senha ---
    @Test
    void tc019_2_deveReativarContaAutomaticamenteAoRedefinirSenha() {
        Usuario usuario = criarFornecedorPadrao();
        usuario.setAtivo(false); 
        String email = usuario.getEmail();
        String novaSenha = "NovaSenha2024";

        when(repository.findByEmail(email)).thenReturn(usuario);

        // Ação: Definir nova senha
        service.redefinirSenhaEReativar(email, novaSenha);

        assertTrue(usuario.isAtivo()); 
        assertEquals(novaSenha, usuario.getSenha());
        verify(repository).save(usuario);
    }
}