package com.crediya.r2dbc.helper;

import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeReactiveRepositoryAdapter implements LoanTypeRepository {
    private final LoanTypeReactiveRepository repository;
    private final ObjectMapper mapper;
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<LoanType> findById(Long id) {
        return repository.findById(id)  // devuelve Mono<LoanTypeEntity>
                .map(entity -> mapper.map(entity, LoanType.class)); // lo transformas a LoanType
    }
}
