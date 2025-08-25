package com.crediya.api.dtos;

import java.time.LocalDate;

public record EditLoanDTO(
        Long dniClient,
        Double amount,
        LocalDate deadLine,
        Long loanTypeId) {
}
