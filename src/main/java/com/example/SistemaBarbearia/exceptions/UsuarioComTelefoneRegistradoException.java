package com.example.SistemaBarbearia.exceptions;

public class UsuarioComTelefoneRegistradoException extends RuntimeException {

    public UsuarioComTelefoneRegistradoException(String telefone) {
        super(String.format("Usuário com telefone '%s' já registrado.", telefone));
    }
}
