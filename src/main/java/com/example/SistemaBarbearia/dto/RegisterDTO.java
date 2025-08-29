package com.example.SistemaBarbearia.dto;

import com.example.SistemaBarbearia.entity.TipoUsuario;

public record RegisterDTO(
        String nome,
        String email,
        String telefone,
        String senha,
        TipoUsuario tipoUsuario) {
}
