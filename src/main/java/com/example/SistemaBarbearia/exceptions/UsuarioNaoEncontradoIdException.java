package com.example.SistemaBarbearia.exceptions;

public class UsuarioNaoEncontradoIdException extends RuntimeException {
  public UsuarioNaoEncontradoIdException(String id) {
    super(String.format("Usuário com id '%s' não encontrado.", id));
  }
}
