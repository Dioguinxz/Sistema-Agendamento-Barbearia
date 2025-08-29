package com.example.SistemaBarbearia.repository;

import com.example.SistemaBarbearia.entity.Agendamento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {
    List<Agendamento> findByUsuarioId(String usuarioId);
    List<Agendamento> findByBarbeiroId(String barbeiroId);
    List<Agendamento> findByBarbeiroIdAndHorarioBetween(String barbeiroId, LocalDateTime start, LocalDateTime end);


}
