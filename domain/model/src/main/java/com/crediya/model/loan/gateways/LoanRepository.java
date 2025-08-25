package com.crediya.model.loan.gateways;

import com.crediya.model.loan.Loan;
import reactor.core.publisher.Mono;

public interface LoanRepository {
    Mono<Loan> saveLoan(Loan user);
    Mono<Loan> editLoan(Loan user);
}
