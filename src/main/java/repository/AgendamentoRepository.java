package repository;

import entity.Agendamento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {
    List<Agendamento> findByUsuarioId(String usuarioId);
    List<Agendamento> findByBarbeiroId(String barbeiroId);


}
