package com.example.SistemaBarbearia.infra;

import com.example.SistemaBarbearia.exceptions.UsuarioRegistradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UsuarioRegistradoException.class)
    private ResponseEntity<RestErrorMessage> usuarioRegistradoHandler(UsuarioRegistradoException exception) {
        RestErrorMessage mensagemTratada = new RestErrorMessage(HttpStatus.CONFLICT, exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mensagemTratada);
    }

}
