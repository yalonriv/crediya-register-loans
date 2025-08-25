package com.crediya.r2dbc.helper;

import com.crediya.model.loan.Loan;
import com.crediya.model.loan.gateways.LoanRepository;
import com.crediya.r2dbc.entities.LoanEntity;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanReactiveRepositoryAdapter implements LoanRepository { // ← Solo implementa la interfaz

    private final LoanReactiveRepository repository;
    private final ObjectMapper mapper;

    public LoanReactiveRepositoryAdapter(LoanReactiveRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Loan> saveLoan(Loan loan) {
        LoanEntity entity = mapper.map(loan, LoanEntity.class);
        return repository.save(entity)
                .map(saved -> mapper.map(saved, Loan.class));
    }

    @Override
    public Mono<Loan> editLoan(Loan loan) {
        LoanEntity entity = mapper.map(loan, LoanEntity.class);
        return repository.save(entity)
                .map(updated -> mapper.map(updated, Loan.class));
    }


}