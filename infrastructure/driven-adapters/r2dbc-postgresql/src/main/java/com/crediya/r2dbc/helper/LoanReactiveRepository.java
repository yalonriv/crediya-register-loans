package com.crediya.r2dbc.helper;

import com.crediya.r2dbc.entities.LoanEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanReactiveRepository extends
        ReactiveCrudRepository<LoanEntity, Long>,
        org.springframework.data.repository.query.ReactiveQueryByExampleExecutor<LoanEntity>{
}
