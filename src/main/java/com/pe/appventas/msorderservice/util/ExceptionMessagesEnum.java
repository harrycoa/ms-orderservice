package com.pe.appventas.msorderservice.util;

public enum ExceptionMessagesEnum {
    ACCOUNT_NOT_FOUND("Cuenta no encontrada"),
    INCORRECT_REQUEST_EMPTY_ITEMS_ORDER("Items vacios no permitido en el Order");

    ExceptionMessagesEnum(String msg){
        value = msg;
    }

    private final String value;

    public String  getValue(){
        return value;
    }
}
