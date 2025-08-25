package com.crediya.model.exceptions;

public class LoanTypeNotFoundException extends DomainException {
    public LoanTypeNotFoundException(Long loanTypeId) {
        super("TIPO_PRESTAMO_NO_EXISTE", "Tipo de préstamo con ID " + loanTypeId + " no existe");
    }
}
