package com.example.SistemaBarbearia.exceptions;

public class UsuarioComEmailRegistradoException extends RuntimeException {

    public UsuarioComEmailRegistradoException(String email) {
        super(String.format("Usuário com o e-mail '%s' já registrado.", email));
    }

}