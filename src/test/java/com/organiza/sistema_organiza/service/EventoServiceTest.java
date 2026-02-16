package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Evento;
import com.organiza.sistema_organiza.repository.EventoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTest {

    @InjectMocks
    private EventoService eventoService;

    @Mock
    private EventoRepository eventoRepository;

    @Test
    public void tc01_deveCadastrarEventoComDadosValidos() {
        Evento evento = new Evento("Workshop Java", "Aprenda Spring", LocalDate.now().plusDays(1), LocalTime.of(9,0), LocalDate.now().plusDays(1), LocalTime.of(17,0), "Auditório");

        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        Evento salvo = eventoService.cadastrarEvento(evento);

        Assertions.assertNotNull(salvo);
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    public void tc02_naoDeveCadastrarEventoSemTitulo() {
        Evento evento = new Evento(null, "Desc", LocalDate.now(), LocalTime.now(), LocalDate.now(), LocalTime.now(), "Local");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(evento);
        });

        Assertions.assertEquals("O título é obrigatório", exception.getMessage());
    }

    @Test
    public void tc03_naoDeveCadastrarEventoSemDescricao() {
        Evento evento = new Evento("Testes", null, LocalDate.now(), LocalTime.now(), LocalDate.now(), LocalTime.now(), "Local");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(evento);
        });

        Assertions.assertEquals("A descrição é obrigatória", exception.getMessage());
    }

    @Test
    public void tc04_naoDeveCadastrarEventoSemDataInicio() {
        Evento evento = new Evento("Titulo", "Desc", null, LocalTime.now(), LocalDate.now(), LocalTime.now(), "Local");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(evento);
        });

        Assertions.assertEquals("A data de inicio é obrigatória", exception.getMessage());
    }

    @Test
    public void tc05_naoDeveCadastrarEventoSemHoraInicial() {
        Evento evento = new Evento("Titulo", "Desc", LocalDate.now(), null, LocalDate.now(), LocalTime.now(), "Local");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(evento);
        });

        Assertions.assertEquals("A hora de inicio é obrigatória", exception.getMessage());
    }

    @Test
    public void tc06_naoDeveCadastrarEventoSemDataTermino() {
        Evento evento = new Evento("Titulo", "Desc", LocalDate.now(), LocalTime.now(), null, LocalTime.now(), "Local");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(evento);
        });

        Assertions.assertEquals("A data de termino é obrigatória", exception.getMessage());
    }

    @Test
    public void tc07_naoDeveCadastrarEventoSemHoraTermino() {
        Evento evento = new Evento("Titulo", "Desc", LocalDate.now(), LocalTime.now(), LocalDate.now(), null, "Local");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(evento);
        });

        Assertions.assertEquals("A hora de termino é obrigatória", exception.getMessage());
    }

    @Test
    public void tc08_naoDeveCadastrarEventoSemLocal() {
        Evento evento = new Evento("Titulo", "Desc", LocalDate.now(), LocalTime.now(), LocalDate.now(), LocalTime.now(), null);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            eventoService.cadastrarEvento(evento);
        });

        Assertions.assertEquals("O local é obrigatório", exception.getMessage());
    }

    @Test
    public void tc10_deveVisualizarDetalhesDoEventoComSucesso() {
        Long idEvento = 1L;

        Evento eventoNoBanco = new Evento(
                "Workshop de Desenvolvimento Web",
                "Evento sobre práticas modernas de frontend",
                LocalDate.of(2025, 11, 20),
                LocalTime.of(9, 0),
                LocalDate.of(2025, 11, 20),
                LocalTime.of(18, 0),
                "Auditório Central - Bloco A"
        );

        when(eventoRepository.findById(idEvento)).thenReturn(java.util.Optional.of(eventoNoBanco));

        Evento resultado = eventoService.buscarEventoPorId(idEvento);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Workshop de Desenvolvimento Web", resultado.getTitulo());
        Assertions.assertEquals("Evento sobre práticas modernas de frontend", resultado.getDescricao());
        Assertions.assertEquals(LocalDate.of(2025, 11, 20), resultado.getDataInicio());
        Assertions.assertEquals("Auditório Central - Bloco A", resultado.getLocal());
    }

    @Test
    public void tc11_deveExcluirEventoExistente() {
        Long idEvento = 1L;
        when(eventoRepository.existsById(idEvento)).thenReturn(true);

        eventoService.excluirEvento(idEvento);

        verify(eventoRepository, times(1)).deleteById(idEvento);
    }
}
