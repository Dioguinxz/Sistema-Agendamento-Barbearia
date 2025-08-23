package com.example.SistemaBarbearia.exceptions;

public class UsuarioRegistradoException extends RuntimeException {

    public UsuarioRegistradoException() {
        super("Usuário já resgistrado!");
    }

    public UsuarioRegistradoException(String message) {
        super(message);
    }
}
