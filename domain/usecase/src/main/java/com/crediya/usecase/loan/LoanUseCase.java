package com.crediya.usecase.loan;

import com.crediya.model.enums.StatusEnum;
import com.crediya.model.exceptions.InvalidStatusException;
import com.crediya.model.exceptions.LoanTypeNotFoundException;
import com.crediya.model.loan.Loan;
import com.crediya.model.loan.gateways.LoanRepository;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;



@RequiredArgsConstructor
public class LoanUseCase {
    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<Loan> createLoan(Loan loan) {
        if (loan.getStatus() == null) {
            return Mono.error(new InvalidStatusException("Status cannot be null"));
        }
        return loanRepository.saveLoan(loan);
    }

    public Mono<Loan> updateLoan(Loan loan) {
        return loanRepository.editLoan(loan);
    }

    public Mono<LoanType> findById(Long id) {
        return loanTypeRepository.findById(id)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(id)));
    }


}
