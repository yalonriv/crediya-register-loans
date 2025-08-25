package com.crediya.model.exceptions;

public class ClientNotFoundException extends DomainException {
    public ClientNotFoundException() {
        super("CLIENTE_NO_EXISTE", "El cliente no existe");
    }
}