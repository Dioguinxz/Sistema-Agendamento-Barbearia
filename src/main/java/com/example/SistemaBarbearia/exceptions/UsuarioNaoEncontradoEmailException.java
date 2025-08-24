package com.example.SistemaBarbearia.exceptions;

public class UsuarioNaoEncontradoEmailException extends RuntimeException {

    public UsuarioNaoEncontradoEmailException(String email) {
        super(String.format("Usuário com o e-mail '%s' não encontrado.", email));
    }



}
