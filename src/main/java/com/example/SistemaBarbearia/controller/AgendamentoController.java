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

}
