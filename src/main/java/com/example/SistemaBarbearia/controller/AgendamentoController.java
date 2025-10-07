package com.example.SistemaBarbearia.controller;

import com.example.SistemaBarbearia.dto.AgendamentoRequestDTO;
import com.example.SistemaBarbearia.dto.AgendamentoResponseDTO;
import com.example.SistemaBarbearia.dto.AgendamentoUpdateDTO;
import com.example.SistemaBarbearia.entity.Usuario;
import com.example.SistemaBarbearia.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<AgendamentoResponseDTO> agendar(@RequestBody @Valid AgendamentoRequestDTO dto,
                                                          @AuthenticationPrincipal Usuario clienteLogado) {
        AgendamentoResponseDTO agendamentoCriado = agendamentoService.criarAgendamento(dto, clienteLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoCriado);
    }

    @GetMapping("/meus")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarMeusAgendamentos(@AuthenticationPrincipal Usuario clienteLogado) {
        List<AgendamentoResponseDTO> agendamentos = agendamentoService.listarMeusAgendamentos(clienteLogado.getId());
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/todos")
    @PreAuthorize("hasAuthority('BARBEIRO')")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarTodosAgendamentos() {
        List<AgendamentoResponseDTO> agendamentos = agendamentoService.listarTodosAgendamentos();
        return ResponseEntity.ok(agendamentos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<AgendamentoResponseDTO> atualizarAgendamento(
            @PathVariable String id,
            @RequestBody @Valid AgendamentoUpdateDTO dto,
            @AuthenticationPrincipal Usuario clienteLogado) {

        AgendamentoResponseDTO agendamentoAtualizado = agendamentoService.atualizarAgendamento(id, dto, clienteLogado);
        return ResponseEntity.ok(agendamentoAtualizado);
    }

    /**
     * Endpoint para um CLIENTE cancelar seu próprio agendamento.
     * Semanticamente, a "exclusão" de um agendamento pelo cliente é um cancelamento.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<Void> cancelarAgendamentoPeloCliente(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario clienteLogado) {

        agendamentoService.cancelarPeloCliente(id, clienteLogado);

        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para um BARBEIRO cancelar um agendamento de sua agenda.
     */
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('BARBEIRO')")
    public ResponseEntity<Void> cancelarAgendamentoPeloBarbeiro(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario barbeiroLogado) {

        agendamentoService.cancelarPeloBarbeiro(id, barbeiroLogado);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para um BARBEIRO marcar um agendamento como CONCLUÍDO.
     * Acessível apenas por usuários com a permissão 'BARBEIRO'.
     *
     * @param id             O ID do agendamento a ser concluído.
     * @param barbeiroLogado O usuário BARBEIRO autenticado, injetado pelo Spring Security.
     * @return ResponseEntity com status 204 No Content para indicar sucesso.
     */
    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasAuthority('BARBEIRO')")
    public ResponseEntity<Void> concluirAgendamento(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario barbeiroLogado) {

        agendamentoService.marcarComoConcluido(id, barbeiroLogado);
        return ResponseEntity.noContent().build();
    }

}
