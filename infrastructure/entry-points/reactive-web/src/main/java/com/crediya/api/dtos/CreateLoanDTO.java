package com.crediya.api.dtos;

import java.time.LocalDate;

public record CreateLoanDTO(
        Long dniClient,
        Double amount,
        LocalDate deadLine,
        Long loanTypeId,
        String status
) {
}
