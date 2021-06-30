package com.pe.appventas.msorderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class PaymentNotAcceptedException extends Exception{
    public PaymentNotAcceptedException(String message) {
        super(message);
    }
}
