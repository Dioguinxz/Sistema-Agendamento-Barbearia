package com.example.SistemaBarbearia.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CancelamentoForaDoPrazoException extends RuntimeException {
    public CancelamentoForaDoPrazoException(String message) {
        super(message);
    }
}
