package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Avaliacao;
import com.organiza.sistema_organiza.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public Avaliacao avaliarFornecedor(Avaliacao avaliacao) {
        if (avaliacao.getEstrelas() == null) {
            throw new IllegalArgumentException("A quantidade de estrelas é obrigatória");
        }

        LocalDateTime terminoEvento = LocalDateTime.of(
                avaliacao.getEvento().getDataTermino(),
                avaliacao.getEvento().getHoraTermino()
        );

        if (terminoEvento.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Você só pode avaliar fornecedores após a conclusão do evento.");
        }

        return avaliacaoRepository.save(avaliacao);
    }
}