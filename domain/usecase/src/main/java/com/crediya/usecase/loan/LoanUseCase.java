package com.crediya.usecase.loan;

import com.crediya.model.enums.StatusEnum;
import com.crediya.model.exceptions.InvalidStatusException;
import com.crediya.model.loan.Loan;
import com.crediya.model.loan.gateways.LoanRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class LoanUseCase {
    private final LoanRepository loanRepository;

    public Mono<Loan> createLoan(Loan loan) {
        return validateStatus(loan.getStatus())
                .then(loanRepository.saveLoan(loan));
    }
    public Mono<Loan> updateLoan(Loan loan) {
        return loanRepository.editLoan(loan);
    }



    private Mono<Void> validateStatus(StatusEnum status) {
        if (status == null) {
            return Mono.error(new InvalidStatusException("null"));
        }

        try {
            StatusEnum.valueOf(status.name()); // Validar que sea un enum válido
            return Mono.empty();
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidStatusException(status.name()));
        }
    }


}
