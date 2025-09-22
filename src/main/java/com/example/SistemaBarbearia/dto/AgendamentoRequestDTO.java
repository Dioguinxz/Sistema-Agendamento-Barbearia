package com.example.SistemaBarbearia.dto;

import com.example.SistemaBarbearia.entity.TipoServico;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendamentoRequestDTO(@NotBlank(message = "O ID do barbeiro é obrigatório") String barbeiroId,
                                    @NotNull(message = "O tipo de serviço é obrigatório") TipoServico tipoServico,
                                    @NotNull(message = "O horário é obrigatório")
                                    @Future(message = "O horário do agendamento deve ser uma data futura") LocalDateTime horario) {
}
