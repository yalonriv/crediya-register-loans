package com.crediya.api;

import com.crediya.api.dtos.CreateLoanDTO;
import com.crediya.api.dtos.EditLoanDTO;
import com.crediya.api.dtos.ErrorResponse; // ← Solo este import de ErrorResponse
import com.crediya.api.mapper.LoanDTOMapper;
import com.crediya.api.user.UserClient;
import com.crediya.model.exceptions.ClientNotFoundException;
import com.crediya.model.exceptions.DomainException;
import com.crediya.model.exceptions.LoanTypeNotFoundException;
import com.crediya.model.loan.Loan;
import com.crediya.usecase.loan.LoanUseCase;
import com.crediya.usecase.loantype.LoanTypeUseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class Handler {

    private final LoanUseCase loanUseCase;
    private final LoanTypeUseCase loanTypeUseCase;
    private final LoanDTOMapper loanMapper;
    private final UserClient userClient;

    public Handler(LoanUseCase loanUseCase,
                   LoanTypeUseCase loanTypeUseCase,
                   LoanDTOMapper loanMapper,
                   UserClient userClient) {
        this.loanUseCase = loanUseCase;
        this.loanTypeUseCase = loanTypeUseCase;
        this.loanMapper = loanMapper;
        this.userClient = userClient;
    }

    // Crear préstamo
    public Mono<ServerResponse> createLoan(ServerRequest request) {
        return request.bodyToMono(CreateLoanDTO.class)
                .flatMap(dto ->
                        Mono.zip(
                                loanTypeUseCase.findById(dto.loanTypeId())
                                        .switchIfEmpty(Mono.error(new LoanTypeNotFoundException(dto.loanTypeId()))),
                                userClient.existsByDni(dto.dniClient())
                                        .flatMap(exists -> exists ?
                                                Mono.just(true) :
                                                Mono.error(new ClientNotFoundException())),
                                Mono.just(dto)
                        )
                )
                .flatMap(tuple -> {
                    var loanType = tuple.getT1();
                    var exists = tuple.getT2();
                    var dto = tuple.getT3();

                    Loan loan = loanMapper.toModel(dto);
                    return loanUseCase.createLoan(loan);
                })
                .flatMap(loan -> ServerResponse.ok().bodyValue(loan))
                .onErrorResume(this::handleError);
    }

    private Mono<ServerResponse> handleError(Throwable error) {
        if (error instanceof DomainException domainEx) {
            return ServerResponse.status(400)
                    .bodyValue(new ErrorResponse(domainEx.getCode(), domainEx.getMessage()));
        } else if (error instanceof IllegalArgumentException) {
            return ServerResponse.status(400)
                    .bodyValue(new ErrorResponse("DATOS_INVALIDOS", error.getMessage()));
        } else {
            return ServerResponse.status(500)
                    .bodyValue(new ErrorResponse("ERROR_INTERNO", "Error del servidor"));
        }
    }


    // Editar préstamo
    public Mono<ServerResponse> editLoan(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(EditLoanDTO.class)
                .flatMap(dto ->
                        loanTypeUseCase.findById(dto.loanTypeId())
                                .switchIfEmpty(Mono.error(new RuntimeException("Tipo de préstamo no existe")))
                                .flatMap(type -> {
                                    Loan loan = loanMapper.toModel(dto);
                                    loan.setId(id); // importante asignar el id para la actualización
                                    return loanUseCase.updateLoan(loan);
                                })
                )
                .flatMap(loan -> ServerResponse.ok().bodyValue(loan))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
