package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Usuario;
import com.organiza.sistema_organiza.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    // Para testes, você vai usar @Mock aqui
    @Autowired
    private BuscaCepService buscaCepService;

    @Autowired
    private EnviadorEmailService emailService;

    public Usuario cadastrarUsuario(Usuario usuario) {
        // REGRA 1: Validação básica de campos
        if (usuario.getNome() == null || usuario.getEmail() == null) {
            throw new IllegalArgumentException("Dados obrigatórios faltando");
        }

        // REGRA 2: Validação de Senha (min 8, max 20)
        if (usuario.getSenha().length() < 8 || usuario.getSenha().length() > 20) {
            throw new IllegalArgumentException("Senha deve ter entre 8 e 20 caracteres");
        }

        // REGRA 3: Email único
        if (repository.findByEmail(usuario.getEmail()) != null) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // REGRA 4: Fornecedor precisa de endereço
        if ("FORNECEDOR".equals(usuario.getTipo())) {
            if (usuario.getCep() == null) {
                throw new IllegalArgumentException("Fornecedor deve informar CEP");
            }
            // Simula busca automática
            String logradouro = buscaCepService.buscarPorCep(usuario.getCep());
            usuario.setEnderecoCompleto(logradouro + ", Nº " + "S/N"); // Simplificação
        }

        // REGRA 5: Gerar código e enviar "email"
        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
        usuario.setCodigoVerificacao(codigo);
        usuario.setAtivo(false);

        // Salva
        Usuario salvo = repository.save(usuario);

        // Envia email (apenas log)
        emailService.enviarCodigo(salvo.getEmail(), codigo);

        return salvo;
    }
}
