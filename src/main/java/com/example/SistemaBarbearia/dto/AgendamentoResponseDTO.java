package com.example.SistemaBarbearia.dto;

import com.example.SistemaBarbearia.entity.Agendamento;
import com.example.SistemaBarbearia.entity.StatusAgendamento;
import com.example.SistemaBarbearia.entity.TipoServico;

import java.time.LocalDateTime;

public record AgendamentoResponseDTO(
        String id,
        String usuarioId,
        String barbeiroId,
        String nomeCliente,
        String nomeBarbeiro,
        TipoServico tipoServico,
        LocalDateTime horario,
        StatusAgendamento status
) {

    public AgendamentoResponseDTO(Agendamento agendamento) {
        this(
                agendamento.getId(),
                agendamento.getUsuarioId(),
                agendamento.getBarbeiroId(),
                agendamento.getNomeCliente(),
                agendamento.getNomeBarbeiro(),
                agendamento.getTipoServico(),
                agendamento.getHorario(),
                agendamento.getStatus()
        );
    }
}