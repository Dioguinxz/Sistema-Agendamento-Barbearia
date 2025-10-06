package com.example.SistemaBarbearia.dto;

import com.example.SistemaBarbearia.entity.TipoUsuario;
import com.example.SistemaBarbearia.entity.Usuario;

public record UsuarioResponseDTO(
        String id,
        String nome,
        String telefone,
        String email,
        TipoUsuario tipo
) {

    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getTelefone(),
                usuario.getEmail(),
                usuario.getTipo()
        );
    }
}