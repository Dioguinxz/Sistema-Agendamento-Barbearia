package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "agendamentos")
@Data // Injeta Getters e Setters
@AllArgsConstructor // Gera um construtor com todos os campos
@NoArgsConstructor // Gera um construtor sem argumentos
public class Agendamento {

    @Id
    private ObjectId id;
    private int usuarioId;
    private int barbeiroId;
    private TipoServico tipoServico;
    private LocalDateTime horario;
    private StatusAgendamento status;




}
