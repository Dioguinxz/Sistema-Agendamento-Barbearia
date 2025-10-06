package com.example.SistemaBarbearia.infra;

import com.example.SistemaBarbearia.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UsuarioComEmailRegistradoException.class)
    private ResponseEntity<RestErrorMessage> usuarioEmailRegistradoHandler(UsuarioComEmailRegistradoException exception) {
        RestErrorMessage mensagemTratada = new RestErrorMessage(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mensagemTratada);
    }

    @ExceptionHandler(UsuarioComTelefoneRegistradoException.class)
    private ResponseEntity<RestErrorMessage> usuarioTelefoneRegistradoHandler(UsuarioComTelefoneRegistradoException exception) {
        RestErrorMessage mensagemTratada = new RestErrorMessage(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mensagemTratada);
    }

    @ExceptionHandler(UsuarioNaoEncontradoEmailException.class)
    private ResponseEntity<RestErrorMessage> usuarioNaoEncontradoHandler(UsuarioNaoEncontradoEmailException exception) {
        RestErrorMessage mensagemTratada = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensagemTratada);
    }

    @ExceptionHandler(UsuarioNaoEncontradoIdException.class)
    private ResponseEntity<RestErrorMessage> usuarioNaoEncontradoIdHandler(UsuarioNaoEncontradoIdException exception) {
        RestErrorMessage mensagemTratada = new RestErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensagemTratada);
    }

    @ExceptionHandler(UsuarioNaoAlteraTipoException.class)
    private ResponseEntity<RestErrorMessage> usuarioNaoAlteraTipoHandler(UsuarioNaoAlteraTipoException exception) {
        RestErrorMessage mensagemTratada = new RestErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemTratada);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestErrorMessage> handleBadCredentials(BadCredentialsException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String mensagem = "E-mail ou senha inválidos.";
        RestErrorMessage errorResponse = new RestErrorMessage(status, mensagem);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(AgendamentoException.class)
    public ResponseEntity<RestErrorMessage> handleAgendamentoException(AgendamentoException exception) {
        RestErrorMessage mensagemTratada = new RestErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemTratada);
    }

}
