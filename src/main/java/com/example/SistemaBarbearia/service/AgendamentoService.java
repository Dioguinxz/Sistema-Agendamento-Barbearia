package com.example.SistemaBarbearia.service;

import com.example.SistemaBarbearia.dto.AgendamentoRequestDTO;
import com.example.SistemaBarbearia.dto.AgendamentoResponseDTO;
import com.example.SistemaBarbearia.entity.*;
import com.example.SistemaBarbearia.repository.AgendamentoRepository;
import com.example.SistemaBarbearia.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO dto, Usuario cliente) {
        // 1. Validar se o horário solicitado é um slot válido (00 ou 30 minutos)
        if (dto.horario().getMinute() != 0 && dto.horario().getMinute() != 30) {
            throw new RuntimeException("Horário inválido. Agendamentos são permitidos apenas em intervalos de 30 minutos (ex: 15:00, 15:30).");
        }

        // 2. Validar se o barbeiroId é de um usuário que realmente é um barbeiro
        Usuario barbeiro = usuarioRepository.findById(dto.barbeiroId())
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));
        if (barbeiro.getTipo() != TipoUsuario.BARBEIRO) {
            throw new RuntimeException("O ID fornecido не pertence a um barbeiro.");
        }

        // 3. Definir a duração de cada serviço e calcular o horário final do agendamento
        int duracaoServico = getDuracaoPorTipo(dto.tipoServico());
        LocalDateTime horarioInicioNovo = dto.horario();
        LocalDateTime horarioFimNovo = horarioInicioNovo.plusMinutes(duracaoServico);

        // 4. Buscar agendamentos próximos para verificar conflitos
        List<Agendamento> agendamentosProximos = agendamentoRepository.findByBarbeiroIdAndHorarioBetween(
                dto.barbeiroId(),
                horarioInicioNovo.minusHours(2), // Pega uma janela maior para garantir
                horarioInicioNovo.plusHours(2)
        );


        for (Agendamento agendamentoExistente : agendamentosProximos) {
            LocalDateTime horarioInicioExistente = agendamentoExistente.getHorario();
            int duracaoExistente = getDuracaoPorTipo(agendamentoExistente.getTipoServico());
            LocalDateTime horarioFimExistente = horarioInicioExistente.plusMinutes(duracaoExistente);


            if (horarioInicioNovo.isBefore(horarioFimExistente) && horarioFimNovo.isAfter(horarioInicioExistente)) {
                throw new RuntimeException("Conflito de horário. O período solicitado já está ocupado.");
            }
        }


        Agendamento novoAgendamento = new Agendamento();
        novoAgendamento.setUsuarioId(cliente.getId());
        novoAgendamento.setBarbeiroId(dto.barbeiroId());
        novoAgendamento.setNomeCliente(cliente.getNome());
        novoAgendamento.setNomeBarbeiro(barbeiro.getNome());

        novoAgendamento.setTipoServico(dto.tipoServico());
        novoAgendamento.setHorario(dto.horario());
        novoAgendamento.setStatus(StatusAgendamento.AGENDADO);

        Agendamento agendamentoSalvo = agendamentoRepository.save(novoAgendamento);

        return new AgendamentoResponseDTO(agendamentoSalvo);
    }

    // Método auxiliar para definir a duração de cada serviço em minutos
    private int getDuracaoPorTipo(TipoServico tipoServico) {
        return switch (tipoServico) {
            case CORTE, BARBA -> 30; // CORTE e BARBA duram 30 minutos
            case CORTE_E_BARBA -> 60;   // CORTE_E_BARBA dura 60 minutos (2 slots)
        };
    }

    // ... outros métodos do serviço (verMeusAgendamentos, verTodosAgendamentos) ...
    public List<AgendamentoResponseDTO> verMeusAgendamentos(String clienteId) {
        return agendamentoRepository.findByUsuarioId(clienteId).stream()
                .map(AgendamentoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<AgendamentoResponseDTO> verTodosAgendamentos() {
        return agendamentoRepository.findAll().stream()
                .map(AgendamentoResponseDTO::new)
                .collect(Collectors.toList());
    }
}
