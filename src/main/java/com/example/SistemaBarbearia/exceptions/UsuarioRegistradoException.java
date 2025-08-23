package com.example.SistemaBarbearia.exceptions;

public class UsuarioRegistradoException extends RuntimeException {

    public UsuarioRegistradoException(String email) {
        super(String.format("Usuário com o e-mail '%s' já registrado.", email));
    }
}