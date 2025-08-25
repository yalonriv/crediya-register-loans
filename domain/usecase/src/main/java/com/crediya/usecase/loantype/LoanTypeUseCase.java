package com.crediya.usecase.loantype;

import com.crediya.model.exceptions.LoanTypeNotFoundException;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanTypeUseCase {
    private final LoanTypeRepository loanTypeRepository;

    public Mono<LoanType> findById(Long id) {
        return loanTypeRepository.findById(id)
                .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(id)));
    }
}
