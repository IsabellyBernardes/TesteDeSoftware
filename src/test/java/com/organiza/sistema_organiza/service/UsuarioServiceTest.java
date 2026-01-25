package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Usuario;
import com.organiza.sistema_organiza.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private BuscaCepService buscaCepService;

    @Mock
    private EnviadorEmailService emailService;

    // Método auxiliar para criar um usuário padrão
    private Usuario criarUsuarioPadrao() {
        Usuario u = new Usuario();
        u.setNome("Maria da Silva");
        u.setEmail("maria.silva@testeunico.com");
        u.setSenha("SenhaForte10");
        u.setTelefone("81988887777");
        u.setTipo("FORNECEDOR");
        u.setCnpj("34956433000137"); // CNPJ válido (14 dígitos)
        u.setCep("50730000");
        u.setNumero("101");
        u.setComplemento("Casa");
        return u;
    }

    // --- TC 001: Cadastro com Sucesso  ---
    @Test
    void tc001_deveCadastrarUsuarioComSucesso() {
        Usuario usuario = criarUsuarioPadrao();

        when(buscaCepService.buscarPorCep(anyString())).thenReturn("Rua Teste");
        when(repository.findByEmail(usuario.getEmail())).thenReturn(null);
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario salvo = service.cadastrarUsuario(usuario);

        assertNotNull(salvo);
        verify(emailService, times(1)).enviarCodigo(anyString(), anyString());
    }

    // --- TC 002 a TC 006: Validações Básicas  ---
    @Test
    void tc002_deveFalharQuandoNomeVazio() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setNome("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo nome precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc003_deveFalharQuandoEmailVazio() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setEmail(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo email precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc004_deveFalharQuandoSenhaVazia() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setSenha("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo senha precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc005_deveFalharQuandoTelefoneVazio() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setTelefone(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo telefone precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc006_deveFalharQuandoCnpjVazio() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCnpj("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo cnpj precisa ser preenchido", e.getMessage());
    }

    // --- TC 007, TC 008, TC 009, TC 012, TC 014: Validações de Fornecedor  ---

    @Test
    void tc007_deveFalharQuandoCepVazioParaFornecedor() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCep(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo cep precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc008_deveFalharQuandoNumeroEnderecoVazio() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setNumero("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo numero_endereco precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc009_deveFalharQuandoComplementoVazio() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setComplemento("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo complemento precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc012_deveFalharQuandoCnpjInvalido() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCnpj("111");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo cnpj precisa ser válido", e.getMessage());
    }

    @Test
    void tc014_deveFalharQuandoCepInvalido() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCep("11111111");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, cep inválido, mostrar mascara esperada \"11111-111\"", e.getMessage());
    }

    // --- TC 016: Senha Fraca (COM MOCK de CEP) ---
    @Test
    void tc016_deveFalharQuandoSenhaForaDoPadrao() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setSenha("123");

        when(buscaCepService.buscarPorCep(anyString())).thenReturn("Rua Teste");

        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        // Mensagem atualizada conforme PDF
        assertEquals("Erro, senha não corresponde com padrão do tipo esperado \"Exemplosenha11#\"", e.getMessage());
    }

    // --- TC 013: Email Duplicado (COM MOCKS) ---
    @Test
    void tc013_deveFalharQuandoEmailDuplicado() {
        Usuario usuario = criarUsuarioPadrao();

        when(buscaCepService.buscarPorCep(anyString())).thenReturn("Rua Teste");
        when(repository.findByEmail(usuario.getEmail())).thenReturn(new Usuario()); // Simula existência

        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, email já está sendo utilizado", e.getMessage());
    }

    // --- TC 010 e TC 011: Validação de Código ---
    @Test
    void tc010_deveFalharQuandoCodigoVerificacaoVazio() {
        // Fails immediately, NO MOCK
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.validarCadastro("email@teste.com", ""));
        assertEquals("Erro, campo codigo deve ser preenchido", e.getMessage());
    }

    @Test
    void tc011_deveFalharQuandoCodigoVerificacaoIncorreto() {
        String email = "maria.silva@testeunico.com";
        Usuario usuarioNoBanco = criarUsuarioPadrao();
        usuarioNoBanco.setCodigoVerificacao("123456");

        when(repository.findByEmail(email)).thenReturn(usuarioNoBanco);

        Exception e = assertThrows(IllegalArgumentException.class, () -> service.validarCadastro(email, "errado"));
        assertEquals("Erro, codigo incorreto, mostrar mascara esperada \"123456\"", e.getMessage());
    }
}