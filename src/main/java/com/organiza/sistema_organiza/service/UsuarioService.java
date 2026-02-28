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

    // --- LÓGICA DE INATIVAÇÃO (TC_017, TC_018, TC_019) ---
    private int tentativasInativacao = 0;

    public void inativarConta(String email, String codigo, int meses) {
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado");

        // TC_018 e TC_019: Validação de tentativas 
        if (!usuario.getCodigoVerificacao().equals(codigo)) {
            tentativasInativacao++;
            if (tentativasInativacao >= 3) {
                tentativasInativacao = 0; // Reseta para próxima vez
                throw new IllegalStateException("Limite de tentativas excedido. O processo de inativação foi suspenso.");
            }
            throw new IllegalArgumentException("Código de verificação incorreto. Tente novamente.");
        }

        // TC_017: Sucesso na inativação [cite: 6, 16]
        usuario.setAtivo(false);
        tentativasInativacao = 0;
        repository.save(usuario);
        System.out.println("Conta inativada por " + meses + " meses.");
    }

    // TC_019.2: Reativação automática ao redefinir senha 
    public void redefinirSenhaEReativar(String email, String novaSenha) {
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) throw new IllegalArgumentException("Usuário não encontrado");

        usuario.setSenha(novaSenha); // Aqui você aplicaria a regex de senha forte
        usuario.setAtivo(true);      // Reativa automaticamente 
        repository.save(usuario);
    }
}