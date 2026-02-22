package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Usuario;
import com.organiza.sistema_organiza.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BuscaCepService buscaCepService;

    @Autowired
    private EnviadorEmailService emailService;

    public Usuario cadastrarUsuario(Usuario usuario) {
        // TC002, TC003, TC004, TC005: Validação de campos obrigatórios gerais
        if (usuario.getNome() == null || usuario.getNome().isEmpty())
            throw new IllegalArgumentException("Erro, campo nome precisa ser preenchido");
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty())
            throw new IllegalArgumentException("Erro, campo email precisa ser preenchido");
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty())
            throw new IllegalArgumentException("Erro, campo senha precisa ser preenchido");
        if (usuario.getTelefone() == null || usuario.getTelefone().isEmpty())
            throw new IllegalArgumentException("Erro, campo telefone precisa ser preenchido");

        // Regras específicas para FORNECEDOR (TC006, TC012, TC008, TC009, TC007, TC014)
        if ("FORNECEDOR".equals(usuario.getTipo())) {

            // Validação de CNPJ
            if (usuario.getCnpj() == null || usuario.getCnpj().isEmpty()) {
                throw new IllegalArgumentException("Erro, campo cnpj precisa ser preenchido");
            }
            if (usuario.getCnpj().length() != 14) {
                throw new IllegalArgumentException("Erro, campo cnpj precisa ser válido");
            }

            // Validação de Número do Endereço
            if (usuario.getNumero() == null || usuario.getNumero().isEmpty()) {
                throw new IllegalArgumentException("Erro, campo numero_endereco precisa ser preenchido");
            }

            // Validação de Complemento
            if (usuario.getComplemento() == null || usuario.getComplemento().isEmpty()) {
                throw new IllegalArgumentException("Erro, campo complemento precisa ser preenchido");
            }

            // Validação de CEP
            if (usuario.getCep() == null || usuario.getCep().isEmpty()) {
                throw new IllegalArgumentException("Erro, campo cep precisa ser preenchido");
            }

            // Verifica formato (8 dígitos) e rejeita sequências repetidas como "11111111"
            if (!usuario.getCep().matches("\\d{8}") || usuario.getCep().matches("(\\d)\\1{7}")) {
                throw new IllegalArgumentException("Erro, cep inválido, mostrar mascara esperada \"11111-111\"");
            }

            // Busca endereço e preenche automaticamente
            String logradouro = buscaCepService.buscarPorCep(usuario.getCep());
            // Monta o endereço completo com os dados manuais
            usuario.setEnderecoCompleto(logradouro + ", Nº " + usuario.getNumero() + " (" + usuario.getComplemento() + ")");
        }

        // TC016: Senha Forte (Mínimo 8, Max 20, Letras e Números)
        if (!usuario.getSenha().matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,20}$")) {
            throw new IllegalArgumentException("Erro, senha não corresponde com padrão do tipo esperado \"Exemplosenha11#\"");
        }

        // TC013: Email Duplicado
        if (repository.findByEmail(usuario.getEmail()) != null) {
            throw new IllegalArgumentException("Erro, email já está sendo utilizado");
        }

        // TC001: Sucesso - Gerar código e salvar
        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
        usuario.setCodigoVerificacao(codigo);
        usuario.setAtivo(false);

        Usuario salvo = repository.save(usuario);
        emailService.enviarCodigo(salvo.getEmail(), codigo);

        return salvo;
    }

    // --- NOVO MÉTODO PARA VALIDAR CÓDIGO (TC 010 e TC 011) ---
    public void validarCadastro(String email, String codigoInformado) {
        // TC 010: Código não preenchido
        if (codigoInformado == null || codigoInformado.isEmpty()) {
            throw new IllegalArgumentException("Erro, campo codigo deve ser preenchido");
        }

        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        // TC 011: Código incorreto [cite: 111]
        if (!usuario.getCodigoVerificacao().equals(codigoInformado)) {
            throw new IllegalArgumentException("Erro, codigo incorreto, mostrar mascara esperada \"123456\"");
        }

        // Ativa o usuário se tudo estiver certo
        usuario.setAtivo(true);
        usuario.setCodigoVerificacao(null);
        repository.save(usuario);
    }
    // --- LOGIN - ETAPA 1 (Email + Senha + Geração do Código 2FA) ---
    public void iniciarLogin(String email, String senha, String metodo) {

        // TC_005 - Falha ao logar com ambos os campos vazios
        if ((email == null || email.isEmpty()) && (senha == null || senha.isEmpty()))
            throw new IllegalArgumentException("Erro, campos email e senha precisam ser preenchidos");

        // TC_006 - Falha ao logar com senha vazia
        if (senha == null || senha.isEmpty())
            throw new IllegalArgumentException("Erro, campo senha precisa ser preenchido");

        // TC_007 - Falha ao logar com email em formato inválido
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            throw new IllegalArgumentException("Erro, formato de email inválido");

        // TC_008 - Normalização de email (letras maiúsculas)
        email = email.toLowerCase();

        Usuario usuario = repository.findByEmail(email);

        // TC_010 - Email não cadastrado
        if (usuario == null)
            throw new IllegalArgumentException("Erro, usuário não encontrado");

        // TC_009 - Senha incorreta
        if (!usuario.getSenha().equals(senha))
            throw new IllegalArgumentException("Erro, senha incorreta");

        // TC_011 - Senha case sensitive (já atendido pelo equals)
        // (não precisa código extra, equals já diferencia maiúsculas/minúsculas)

        if (!usuario.isAtivo())
            throw new IllegalArgumentException("Erro, usuário não está ativo");

        // TC_001, TC_002, TC_003, TC_004
        // Geração do código para autenticação de dois fatores
        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);

        usuario.setCodigoLogin2FA(codigo);
        usuario.setMetodo2FA(metodo);
        usuario.setExpiracaoCodigo2FA(System.currentTimeMillis() + 300000); // 5 minutos

        repository.save(usuario);

        if ("EMAIL".equals(metodo)) {
            emailService.enviarCodigo(usuario.getEmail(), codigo);
        } else if ("SMS".equals(metodo)) {
            // Simulação envio SMS
        }
    }
        // --- LOGIN - ETAPA 2 (Validação do Código 2FA) ---
    public void validarCodigoLogin(String email, String codigoInformado) {

        Usuario usuario = repository.findByEmail(email.toLowerCase());

        if (usuario == null)
            throw new IllegalArgumentException("Usuário não encontrado");

        // TC_015 - Código expirado
        if (usuario.getExpiracaoCodigo2FA() == null ||
            System.currentTimeMillis() > usuario.getExpiracaoCodigo2FA()) {
            throw new IllegalArgumentException("Erro, código expirado");
        }

        // TC_012 - Código de Email incorreto
        // TC_013 - Código de SMS incorreto
        if (usuario.getCodigoLogin2FA() == null ||
            !usuario.getCodigoLogin2FA().equals(codigoInformado)) {
            throw new IllegalArgumentException("Erro, código incorreto");
        }

        usuario.setCodigoLogin2FA(null);
        usuario.setExpiracaoCodigo2FA(null);

        repository.save(usuario);
    }
        // --- TC_014 - Sucesso ao utilizar "Reenviar código" ---
    public void reenviarCodigo(String email) {

        Usuario usuario = repository.findByEmail(email.toLowerCase());

        if (usuario == null)
            throw new IllegalArgumentException("Usuário não encontrado");

        String novoCodigo = String.valueOf((int)(Math.random() * 900000) + 100000);

        usuario.setCodigoLogin2FA(novoCodigo);
        usuario.setExpiracaoCodigo2FA(System.currentTimeMillis() + 300000);

        repository.save(usuario);

        if ("EMAIL".equals(usuario.getMetodo2FA())) {
            emailService.enviarCodigo(usuario.getEmail(), novoCodigo);
        } else {
            // Simular envio SMS
        }
    }
        // --- TC_016 - Sucesso ao trocar método de autenticação ---
    public void trocarMetodo2FA(String email, String novoMetodo) {

        Usuario usuario = repository.findByEmail(email.toLowerCase());

        if (usuario == null)
            throw new IllegalArgumentException("Usuário não encontrado");
        
        if (!"EMAIL".equals(novoMetodo) && !"SMS".equals(novoMetodo)) {
            throw new IllegalArgumentException("Método inválido");
}
        usuario.setMetodo2FA(novoMetodo);
        repository.save(usuario);
    }
}