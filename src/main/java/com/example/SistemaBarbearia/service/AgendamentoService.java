package com.example.SistemaBarbearia.service;

import com.example.SistemaBarbearia.dto.AgendamentoRequestDTO;
import com.example.SistemaBarbearia.dto.AgendamentoResponseDTO;
import com.example.SistemaBarbearia.dto.AgendamentoUpdateDTO;
import com.example.SistemaBarbearia.entity.*;
import com.example.SistemaBarbearia.exceptions.AgendamentoException;
import com.example.SistemaBarbearia.repository.AgendamentoRepository;
import com.example.SistemaBarbearia.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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


        // Validação de horário no passado
        if (horarioAgendamento.isBefore(LocalDateTime.now())) {
            throw new AgendamentoException("Não é possível agendar um horário no passado.");
        }

        // Validação do dia da semana (não funciona aos domingos)
        if (horarioAgendamento.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new AgendamentoException("Fora do horário de funcionamento. A barbearia não funciona aos domingos.");
        }

        // Validação da hora do agendamento (das 9h às 18h)
        LocalTime horaAgendamento = horarioAgendamento.toLocalTime();
        LocalTime horarioAbertura = LocalTime.of(9, 0);
        LocalTime horarioFechamento = LocalTime.of(18, 0);

        if (horaAgendamento.isBefore(horarioAbertura) || horaAgendamento.isAfter(horarioFechamento)) {
            throw new AgendamentoException("Fora do horário de funcionamento. A barbearia funciona das 9h às 18h.");
        }


        // Validação de intervalo de agendamento (00 ou 30 minutos)
        if (horarioAgendamento.getMinute() != 0 && horarioAgendamento.getMinute() != 30) {
            throw new AgendamentoException("Horário inválido. Agendamentos são permitidos apenas em intervalos de 30 minutos (ex: 15:00, 15:30).");
        }

        // Validação do barbeiro
        Usuario barbeiro = usuarioRepository.findById(dto.barbeiroId())
                .orElseThrow(() -> new AgendamentoException("Barbeiro não encontrado"));
        if (barbeiro.getTipo() != TipoUsuario.BARBEIRO) {
            throw new AgendamentoException("O ID fornecido não pertence a um barbeiro.");
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
                throw new AgendamentoException("Horário já ocupado.");
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

    public List<AgendamentoResponseDTO> listarMeusAgendamentos(String clienteId) {
        return agendamentoRepository.findByUsuarioId(clienteId).stream()
                .map(AgendamentoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<AgendamentoResponseDTO> listarTodosAgendamentos() {
        return agendamentoRepository.findAll().stream()
                .map(AgendamentoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public AgendamentoResponseDTO atualizarAgendamento(String agendamentoId, AgendamentoUpdateDTO dto, Usuario clienteLogado) {
        Agendamento agendamentoExistente = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new AgendamentoException("Agendamento não encontrado com o ID: " + agendamentoId));

        if (!agendamentoExistente.getUsuarioId().equals(clienteLogado.getId())) {
            throw new AgendamentoException("Você não tem permissão para editar este agendamento.");
        }

        LocalDateTime novoHorario = dto.horario();

        if (novoHorario.isBefore(LocalDateTime.now())) {
            throw new AgendamentoException("Não é possível agendar um horário no passado.");
        }

        if (novoHorario.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new AgendamentoException("Desculpe, a barbearia não funciona aos domingos.");
        }

        LocalTime horaAgendamento = novoHorario.toLocalTime();
        LocalTime horarioAbertura = LocalTime.of(9, 0);
        LocalTime horarioFechamento = LocalTime.of(18, 0);
        if (horaAgendamento.isBefore(horarioAbertura) || horaAgendamento.isAfter(horarioFechamento)) {
            throw new AgendamentoException("Fora do horário de funcionamento. A barbearia funciona das 9h às 18h.");
        }

        if (novoHorario.getMinute() != 0 && novoHorario.getMinute() != 30) {
            throw new AgendamentoException("Horário inválido. Agendamentos são permitidos apenas em intervalos de 30 minutos.");
        }

        Usuario barbeiro = usuarioRepository.findById(dto.barbeiroId())
                .orElseThrow(() -> new AgendamentoException("Barbeiro não encontrado."));
        if (barbeiro.getTipo() != TipoUsuario.BARBEIRO) {
            throw new AgendamentoException("O ID fornecido não pertence a um barbeiro.");
        }

        int duracaoServico = getDuracaoPorTipo(dto.tipoServico());
        LocalDateTime horarioFimNovo = novoHorario.plusMinutes(duracaoServico);

        List<Agendamento> agendamentosProximos = agendamentoRepository.findByBarbeiroIdAndHorarioBetween(
                dto.barbeiroId(),
                novoHorario.minusHours(2),
                novoHorario.plusHours(2)
        );

        for (Agendamento outroAgendamento : agendamentosProximos) {
            if (outroAgendamento.getId().equals(agendamentoId)) {
                continue;
            }

            LocalDateTime horarioInicioExistente = outroAgendamento.getHorario();
            int duracaoExistente = getDuracaoPorTipo(outroAgendamento.getTipoServico());
            LocalDateTime horarioFimExistente = horarioInicioExistente.plusMinutes(duracaoExistente);

            if (novoHorario.isBefore(horarioFimExistente) && horarioFimNovo.isAfter(horarioInicioExistente)) {
                throw new AgendamentoException("Conflito de horário. O período solicitado já está ocupado por outro agendamento.");
            }
        }

        agendamentoExistente.setBarbeiroId(dto.barbeiroId());
        agendamentoExistente.setNomeBarbeiro(barbeiro.getNome());
        agendamentoExistente.setTipoServico(dto.tipoServico());
        agendamentoExistente.setHorario(dto.horario());

        Agendamento agendamentoAtualizado = agendamentoRepository.save(agendamentoExistente);

        return new AgendamentoResponseDTO(agendamentoAtualizado);
    }


}
