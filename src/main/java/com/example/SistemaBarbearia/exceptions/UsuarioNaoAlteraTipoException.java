package com.example.SistemaBarbearia.exceptions;

public class UsuarioNaoAlteraTipoException extends RuntimeException {
    public UsuarioNaoAlteraTipoException() {
        super(String.format("O tipo de usuário não pode ser alterado."));
    }
}
