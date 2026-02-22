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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    private Usuario criarUsuarioPadrao() {
        Usuario u = new Usuario();
        u.setNome("Maria da Silva");
        u.setEmail("maria.silva@testeunico.com");
        u.setSenha("SenhaForte10");
        u.setTelefone("81988887777");
        u.setTipo("FORNECEDOR");
        u.setCnpj("34956433000137");
        u.setCep("50730000");
        u.setNumero("101");
        u.setComplemento("Casa");
        return u;
    }

    // --- TC 001: Cadastro com Sucesso ---
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

    @Test
    void tc001_parte2_deveValidarUsuarioComSucesso() {
    // PASSO 3 DO PDF: "Inserir o código de 6 dígitos recebido por email"
    String email = "maria.silva@testeunico.com";
    String codigoCorreto = "123456"; 
    
    // Preparação: O usuário já existe no banco, mas está INATIVO (ativo = false)
    Usuario usuario = criarUsuarioPadrao();
    usuario.setCodigoVerificacao(codigoCorreto);
    usuario.setAtivo(false); 

    when(repository.findByEmail(email)).thenReturn(usuario);

    //"Clicar em Confirmar Cadastro" 
    service.validarCadastro(email, codigoCorreto);

    // Usuário agora está ATIVO para ir ao Login
    assertTrue(usuario.isAtivo()); // O usuário agora está pronto para fazer Login
    assertNull(usuario.getCodigoVerificacao()); // O código foi consumido
    verify(repository).save(usuario); // As alterações foram salvas no banco
    }

    // --- TC 002: Nome ---
    @Test
    void tc002_deveFalharQuandoNomeNulo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setNome(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo nome precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc002_deveFalharQuandoNomeVazio() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setNome("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo nome precisa ser preenchido", e.getMessage());
    }

    // --- TC 003: Email ---
    @Test
    void tc003_deveFalharQuandoEmailNulo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setEmail(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo email precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc003_deveFalharQuandoEmailVazio() { 
        Usuario usuario = criarUsuarioPadrao();
        usuario.setEmail("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo email precisa ser preenchido", e.getMessage());
    }

    // --- TC 004: Senha ---
    @Test
    void tc004_deveFalharQuandoSenhaNula() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setSenha(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo senha precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc004_deveFalharQuandoSenhaVazia() { 
        Usuario usuario = criarUsuarioPadrao();
        usuario.setSenha("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo senha precisa ser preenchido", e.getMessage());
    }

    // --- TC 005: Telefone ---
    @Test
    void tc005_deveFalharQuandoTelefoneNulo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setTelefone(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo telefone precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc005_deveFalharQuandoTelefoneVazio() { 
        Usuario usuario = criarUsuarioPadrao();
        usuario.setTelefone("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo telefone precisa ser preenchido", e.getMessage());
    }

    // --- TC 006: CNPJ ---
    @Test
    void tc006_deveFalharQuandoCnpjNulo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCnpj(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo cnpj precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc006_deveFalharQuandoCnpjVazio() { 
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCnpj("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo cnpj precisa ser preenchido", e.getMessage());
    }

    // --- TC 007: CEP ---
    @Test
    void tc007_deveFalharQuandoCepNulo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCep(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo cep precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc007_deveFalharQuandoCepVazio() { 
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCep("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo cep precisa ser preenchido", e.getMessage());
    }

    // --- TC 008: Número ---
    @Test
    void tc008_deveFalharQuandoNumeroNulo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setNumero(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo numero_endereco precisa ser preenchido", e.getMessage());
    }

    @Test
    void tc008_deveFalharQuandoNumeroVazio() { 
        Usuario usuario = criarUsuarioPadrao();
        usuario.setNumero("");
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo numero_endereco precisa ser preenchido", e.getMessage());
    }

    // --- TC 009: Complemento ---
    @Test
    void tc009_deveFalharQuandoComplementoNulo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setComplemento(null);
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, campo complemento precisa ser preenchido", e.getMessage());
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

    @Test
    void tc014_parte2_deveFalharQuandoCepTamanhoIncorreto() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCep("12345");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrarUsuario(usuario);
        });

        assertEquals("Erro, cep inválido, mostrar mascara esperada \"11111-111\"", exception.getMessage());
    }

    @Test
    void tc016_deveFalharQuandoSenhaForaDoPadrao() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setSenha("123");

        when(buscaCepService.buscarPorCep(anyString())).thenReturn("Rua Teste");

        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, senha não corresponde com padrão do tipo esperado \"Exemplosenha11#\"", e.getMessage());
    }

    @Test
    void tc013_deveFalharQuandoEmailDuplicado() {
        Usuario usuario = criarUsuarioPadrao();

        when(buscaCepService.buscarPorCep(anyString())).thenReturn("Rua Teste");
        when(repository.findByEmail(usuario.getEmail())).thenReturn(new Usuario());

        Exception e = assertThrows(IllegalArgumentException.class, () -> service.cadastrarUsuario(usuario));
        assertEquals("Erro, email já está sendo utilizado", e.getMessage());
    }

    // --- Validação de Código (TC 010 e TC 011) ---

    @Test
    void tc010_deveFalharQuandoCodigoVerificacaoNulo() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> service.validarCadastro("email@teste.com", null));
        assertEquals("Erro, campo codigo deve ser preenchido", e.getMessage());
    }

    @Test
    void tc010_deveFalharQuandoCodigoVerificacaoVazio() { 
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



//    @Test
//    void tc_extra_deveFalharQuandoUsuarioNaoEncontradoNaValidacao() {
//        String email = "email.inexistente@teste.com";
//        String codigo = "123456";
//
//        when(repository.findByEmail(email)).thenReturn(null);
//
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            service.validarCadastro(email, codigo);
//        });
//
//        assertEquals("Usuário não encontrado", exception.getMessage());
//    }

    // ===================== LOGIN 2FA ======================

    // TC_001 - Logar Fornecedor com sucesso via Email (Autenticação de Dois Fatores)
    @Test
    void TC_001_LogarFornecedorComSucessoViaEmail() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setAtivo(true);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        service.iniciarLogin(usuario.getEmail(), usuario.getSenha(), "EMAIL");

        verify(repository).save(usuario);
        verify(emailService).enviarCodigo(eq(usuario.getEmail()), anyString());
    }

    // TC_002 - Logar Fornecedor com sucesso via SMS (Autenticação de Dois Fatores)
    @Test
    void TC_002_LogarFornecedorComSucessoViaSMS() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setAtivo(true);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        service.iniciarLogin(usuario.getEmail(), usuario.getSenha(), "SMS");

        verify(repository).save(usuario);
    }

    // TC_003 - Logar Organizador com sucesso via Email (Autenticação de Dois Fatores)
    @Test
    void TC_003_LogarOrganizadorComSucessoViaEmail() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setTipo("ORGANIZADOR");
        usuario.setAtivo(true);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        service.iniciarLogin(usuario.getEmail(), usuario.getSenha(), "EMAIL");

        verify(emailService).enviarCodigo(eq(usuario.getEmail()), anyString());
    }

    // TC_004 - Logar Organizador com sucesso via SMS (Autenticação de Dois Fatores)
    @Test
    void TC_004_LogarOrganizadorComSucessoViaSMS() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setTipo("ORGANIZADOR");
        usuario.setAtivo(true);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        service.iniciarLogin(usuario.getEmail(), usuario.getSenha(), "SMS");

        verify(repository).save(usuario);
    }

    // TC_005 - Falha ao logar com ambos os campos vazios
    @Test
    void TC_005_FalhaCamposVazios() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.iniciarLogin("", "", "EMAIL"));

        assertEquals("Erro, campos email e senha precisam ser preenchidos", e.getMessage());
    }

    // TC_006 - Falha ao logar com o campo Senha vazio
    @Test
    void TC_006_FalhaSenhaVazia() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.iniciarLogin("email@teste.com", "", "EMAIL"));

        assertEquals("Erro, campo senha precisa ser preenchido", e.getMessage());
    }

    // TC_007 - Falha ao logar com email em formato inválido
    @Test
    void TC_007_FalhaEmailInvalido() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.iniciarLogin("emailinvalido", "Senha123", "EMAIL"));

        assertEquals("Erro, formato de email inválido", e.getMessage());
    }

    // TC_008 - Sucesso na validação com email contendo letras maiúsculas (Normalização)
    @Test
    void TC_008_SucessoEmailMaiusculoNormalizado() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setAtivo(true);

        when(repository.findByEmail(usuario.getEmail().toLowerCase())).thenReturn(usuario);

        service.iniciarLogin(usuario.getEmail().toUpperCase(), usuario.getSenha(), "EMAIL");

        verify(repository).save(usuario);
    }

    // TC_009 - Falha ao logar com senha incorreta
    @Test
    void TC_009_FalhaSenhaIncorreta() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setAtivo(true);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.iniciarLogin(usuario.getEmail(), "SenhaErrada", "EMAIL"));

        assertEquals("Erro, senha incorreta", e.getMessage());
    }

    // TC_010 - Falha ao logar com email não cadastrado
    @Test
    void TC_010_FalhaEmailNaoCadastrado() {
        when(repository.findByEmail(anyString())).thenReturn(null);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.iniciarLogin("naoexiste@teste.com", "Senha123", "EMAIL"));

        assertEquals("Erro, usuário não encontrado", e.getMessage());
    }

    // TC_011 - Falha ao logar com senha case sensitive incorreta
    @Test
    void TC_011_FalhaSenhaCaseSensitive() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setAtivo(true);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.iniciarLogin(usuario.getEmail(), usuario.getSenha().toLowerCase(), "EMAIL"));

        assertEquals("Erro, senha incorreta", e.getMessage());
    }

    // TC_012 - Falha na Autenticação de Dois Fatores com código de Email incorreto
    @Test
    void TC_012_FalhaCodigoEmailIncorreto() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCodigoLogin2FA("123456");
        usuario.setExpiracaoCodigo2FA(System.currentTimeMillis() + 300000);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.validarCodigoLogin(usuario.getEmail(), "000000"));

        assertEquals("Erro, código incorreto", e.getMessage());
    }

    // TC_013 - Falha na Autenticação de Dois Fatores com código de SMS incorreto
        @Test
    void TC_013_FalhaCodigoSMSIncorreto() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCodigoLogin2FA("123456");
        usuario.setExpiracaoCodigo2FA(System.currentTimeMillis() + 300000);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.validarCodigoLogin(usuario.getEmail(), "000000"));

        assertEquals("Erro, código incorreto", e.getMessage());
    }

    // TC_014 - Sucesso ao utilizar a funcionalidade "Reenviar código"
    @Test
    void TC_014_SucessoReenviarCodigo() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setMetodo2FA("EMAIL");

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        service.reenviarCodigo(usuario.getEmail());

        verify(repository).save(usuario);
        verify(emailService).enviarCodigo(eq(usuario.getEmail()), anyString());
    }

    // TC_015 - Falha na Autenticação de Dois Fatores utilizando um código expirado
    @Test
    void TC_015_FalhaCodigoExpirado() {
        Usuario usuario = criarUsuarioPadrao();
        usuario.setCodigoLogin2FA("123456");
        usuario.setExpiracaoCodigo2FA(System.currentTimeMillis() - 1000);

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.validarCodigoLogin(usuario.getEmail(), "123456"));

        assertEquals("Erro, código expirado", e.getMessage());
    }

    // TC_016 - Sucesso ao trocar o método de Autenticação de Dois Fatores
    @Test
    void TC_016_SucessoTrocarMetodo2FA() {
        Usuario usuario = criarUsuarioPadrao();

        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        service.trocarMetodo2FA(usuario.getEmail(), "SMS");

        assertEquals("SMS", usuario.getMetodo2FA());
        verify(repository).save(usuario);
    }
}
