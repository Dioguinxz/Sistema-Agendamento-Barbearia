package com.example.SistemaBarbearia.service;

import com.example.SistemaBarbearia.dto.AgendamentoRequestDTO;
import com.example.SistemaBarbearia.dto.AgendamentoResponseDTO;
import com.example.SistemaBarbearia.entity.*;
import com.example.SistemaBarbearia.repository.AgendamentoRepository;
import com.example.SistemaBarbearia.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO dto, Usuario cliente) {
        LocalDateTime horarioAgendamento = dto.horario();

        // VALIDAÇÕES DE HORÁRIO DE FUNCIONAMENTO

        // Validação do dia da semana (não funciona aos domingos)
        if (horarioAgendamento.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Desculpe, a barbearia não funciona aos domingos.");
        }

        // Validação da hora do agendamento (das 9h às 18h)
        LocalTime horaAgendamento = horarioAgendamento.toLocalTime();
        LocalTime horarioAbertura = LocalTime.of(9, 0);
        LocalTime horarioFechamento = LocalTime.of(18, 0);

        if (horaAgendamento.isBefore(horarioAbertura) || horaAgendamento.isAfter(horarioFechamento)) {
            throw new RuntimeException("Fora do horário de funcionamento. A barbearia funciona das 9h às 18h.");
        }


        // Validação de intervalo de agendamento (00 ou 30 minutos)
        if (horarioAgendamento.getMinute() != 0 && horarioAgendamento.getMinute() != 30) {
            throw new RuntimeException("Horário inválido. Agendamentos são permitidos apenas em intervalos de 30 minutos (ex: 15:00, 15:30).");
        }

        // Validação do barbeiro
        Usuario barbeiro = usuarioRepository.findById(dto.barbeiroId())
                .orElseThrow(() -> new RuntimeException("Barbeiro não encontrado"));
        if (barbeiro.getTipo() != TipoUsuario.BARBEIRO) {
            throw new RuntimeException("O ID fornecido não pertence a um barbeiro.");
        }

        // Lógica de conflito de horários
        int duracaoServico = getDuracaoPorTipo(dto.tipoServico());
        LocalDateTime horarioInicioNovo = dto.horario();
        LocalDateTime horarioFimNovo = horarioInicioNovo.plusMinutes(duracaoServico);

        List<Agendamento> agendamentosProximos = agendamentoRepository.findByBarbeiroIdAndHorarioBetween(
                dto.barbeiroId(),
                horarioInicioNovo.minusHours(2),
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

        // Criação do agendamento
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
            case CORTE, BARBA -> 30;
            case CORTE_E_BARBA -> 60;
        };
    }


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
