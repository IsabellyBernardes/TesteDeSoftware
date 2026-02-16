package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Avaliacao;
import com.organiza.sistema_organiza.model.Evento;
import com.organiza.sistema_organiza.model.Usuario;
import com.organiza.sistema_organiza.repository.AvaliacaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvaliacaoServiceTest {

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Test
    public void tc12_deveAvaliarFornecedorAposEventoConcluido() {
        Evento eventoPassado = new Evento();
        eventoPassado.setDataTermino(LocalDate.now().minusDays(1));
        eventoPassado.setHoraTermino(LocalTime.of(18, 0));

        Usuario fornecedor = new Usuario();
        Avaliacao avaliacao = new Avaliacao(5, "Ótimo serviço", fornecedor, eventoPassado);

        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        Avaliacao salva = avaliacaoService.avaliarFornecedor(avaliacao);

        Assertions.assertNotNull(salva);
        Assertions.assertEquals(5, salva.getEstrelas());
    }

    @Test
    public void tc13_naoDeveAvaliarSemEstrelas() {
        Evento eventoPassado = new Evento();
        eventoPassado.setDataTermino(LocalDate.now().minusDays(1));
        eventoPassado.setHoraTermino(LocalTime.of(18, 0));

        Avaliacao avaliacao = new Avaliacao(null, "Comentário", new Usuario(), eventoPassado);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            avaliacaoService.avaliarFornecedor(avaliacao);
        });

        Assertions.assertEquals("A quantidade de estrelas é obrigatória", exception.getMessage());
    }

    @Test
    public void tc14_naoDeveAvaliarAntesDoEventoAcabar() {
        Evento eventoFuturo = new Evento();
        eventoFuturo.setDataTermino(LocalDate.now().plusDays(1));
        eventoFuturo.setHoraTermino(LocalTime.of(18, 0));

        Avaliacao avaliacao = new Avaliacao(5, "Tentativa de fraude", new Usuario(), eventoFuturo);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            avaliacaoService.avaliarFornecedor(avaliacao);
        });

        Assertions.assertEquals("Você só pode avaliar fornecedores após a conclusão do evento.", exception.getMessage());
    }
}