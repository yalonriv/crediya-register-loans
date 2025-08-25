package com.crediya.model.exceptions;

public class InvalidStatusException extends DomainException {
    public InvalidStatusException(String status) {
        super("STATUS_INVALIDO", "Status inválido: " + status);
    }
}
