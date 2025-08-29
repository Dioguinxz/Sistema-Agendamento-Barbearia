package com.example.SistemaBarbearia.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "agendamentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Agendamento {

    @Id
    private String id;
    private String usuarioId;
    private String barbeiroId;
    private TipoServico tipoServico;
    private LocalDateTime horario;
    private StatusAgendamento status;

}
