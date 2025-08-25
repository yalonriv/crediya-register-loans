package com.crediya.r2dbc.helper;

import com.crediya.r2dbc.entities.TypeLoanEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<TypeLoanEntity, Long>,
        org.springframework.data.repository.query.ReactiveQueryByExampleExecutor<TypeLoanEntity>{
    Mono<TypeLoanEntity> findById(Long id);
}
