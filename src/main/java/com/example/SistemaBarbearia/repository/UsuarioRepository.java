package com.example.SistemaBarbearia.repository;

import com.example.SistemaBarbearia.entity.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    void deleteByEmail(String email);

    Optional<Usuario> findByTelefone(String telefone);
    void existsByTelefone(String telefone);
}


