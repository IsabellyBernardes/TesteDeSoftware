package com.organiza.sistema_organiza.service;

import com.organiza.sistema_organiza.model.Evento;
import com.organiza.sistema_organiza.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public Evento cadastrarEvento(Evento evento) {
        if (evento.getTitulo() == null || evento.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("O título é obrigatório");
        }
        if (evento.getDescricao() == null || evento.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição é obrigatória");
        }
        if (evento.getDataInicio() == null) {
            throw new IllegalArgumentException("A data de inicio é obrigatória");
        }
        if (evento.getHoraInicio() == null) {
            throw new IllegalArgumentException("A hora de inicio é obrigatória");
        }
        if (evento.getDataTermino() == null) {
            throw new IllegalArgumentException("A data de termino é obrigatória");
        }
        if (evento.getHoraTermino() == null) {
            throw new IllegalArgumentException("A hora de termino é obrigatória");
        }
        if (evento.getLocal() == null || evento.getLocal().trim().isEmpty()) {
            throw new IllegalArgumentException("O local é obrigatório");
        }

        return eventoRepository.save(evento);
    }

    public void excluirEvento(Long id) {
        if(!eventoRepository.existsById(id)) {
            throw new IllegalArgumentException("Evento não encontrado");
        }
        eventoRepository.deleteById(id);
    }

    public Evento buscarEventoPorId(Long id) {
        return eventoRepository.findById(id).orElse(null);
    }
}