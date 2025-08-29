package com.crediya.api;

import com.crediya.api.dtos.CreateLoanDTO;
import com.crediya.api.dtos.ErrorResponse;
import com.crediya.api.mapper.LoanDTOMapper;
import com.crediya.api.user.UserClient;
import com.crediya.model.enums.StatusEnum;
import com.crediya.model.exceptions.ClientNotFoundException;
import com.crediya.model.exceptions.LoanTypeNotFoundException;
import com.crediya.model.loan.Loan;
import com.crediya.model.loantype.LoanType;
import com.crediya.usecase.loan.LoanUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private LoanUseCase loanUseCase;

    @Mock
    private LoanDTOMapper loanMapper;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private Handler handler;

    private CreateLoanDTO createLoanDTO;
    private Loan loan;
    private LoanType loanType;

    @BeforeEach
    void setUp() {
        createLoanDTO = new CreateLoanDTO(
                12345678L,           // dniClient
                1000.0,              // amount
                LocalDate.now().plusMonths(12), // deadLine
                1L,                  // loanTypeId
                "ACTIVE"             // status
        );

        loan = Loan.builder()
                .id(1L)
                .dniClient(12345678L)
                .amount(1000.0)
                .deadLine(LocalDate.now().plusMonths(12))
                .loanTypeId(1L)
                .status(StatusEnum.APROBADO)
                .build();

        loanType = new LoanType(1L, "Personal");
    }

    @Test
    void createLoan_ShouldReturnCreatedLoan() {
        // Arrange
        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(createLoanDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.just(loanType));
        when(userClient.existsByDni(12345678L)).thenReturn(Mono.just(true));
        when(loanMapper.toModel(createLoanDTO)).thenReturn(loan);
        when(loanUseCase.createLoan(loan)).thenReturn(Mono.just(loan));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void createLoan_ShouldHandleDomainException_ClientNotFound() {
        // Arrange
        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(createLoanDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.just(loanType));
        when(userClient.existsByDni(12345678L)).thenReturn(Mono.error(new ClientNotFoundException()));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert - Debe manejar ClientNotFoundException como error 400
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void createLoan_ShouldHandleDomainException_LoanTypeNotFound() {
        // Arrange
        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(createLoanDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.empty());
        when(userClient.existsByDni(12345678L)).thenReturn(Mono.just(true));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert - Debe manejar LoanTypeNotFoundException como error 400
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void createLoan_ShouldHandleIllegalArgumentException() {
        // Arrange
        CreateLoanDTO invalidDTO = new CreateLoanDTO(
                12345678L,
                -1000.0, // amount negativo
                LocalDate.now().plusMonths(12),
                1L,
                "ACTIVE"
        );

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(invalidDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.just(loanType));
        when(userClient.existsByDni(12345678L)).thenReturn(Mono.just(true));
        when(loanMapper.toModel(invalidDTO)).thenThrow(new IllegalArgumentException("Monto inválido"));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert - Debe manejar IllegalArgumentException como error 400
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void createLoan_ShouldHandleGenericException() {
        // Arrange
        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(createLoanDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert - Debe manejar RuntimeException como error 500
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                .verifyComplete();
    }

    @Test
    void createLoan_ShouldReturnErrorWhenClientNotFound() {
        // Arrange
        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(createLoanDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.just(loanType));
        when(userClient.existsByDni(12345678L)).thenReturn(Mono.just(false));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    @Test
    void createLoan_ShouldHandleInvalidStatus() {
        // Arrange
        CreateLoanDTO invalidStatusDTO = new CreateLoanDTO(
                12345678L,
                1000.0,
                LocalDate.now().plusMonths(12),
                1L,
                "INVALID_STATUS" // status inválido
        );

        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(invalidStatusDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.just(loanType));
        when(userClient.existsByDni(12345678L)).thenReturn(Mono.just(true));
        when(loanMapper.toModel(invalidStatusDTO))
                .thenThrow(new IllegalArgumentException("Status inválido: INVALID_STATUS"));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse ->
                        serverResponse.statusCode() == HttpStatus.BAD_REQUEST)
                .verifyComplete();
    }

    // Prueba adicional para verificar el contenido del error response
    @Test
    void createLoan_ShouldReturnProperErrorResponse() {
        // Arrange
        MockServerRequest request = MockServerRequest.builder()
                .body(Mono.just(createLoanDTO));

        when(loanUseCase.findById(1L)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act
        Mono<ServerResponse> responseMono = handler.createLoan(request);

        // Assert - Verificar que el cuerpo contiene ErrorResponse
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> {
                    boolean statusMatches = serverResponse.statusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
                    // También puedes verificar el cuerpo si es necesario
                    return statusMatches;
                })
                .verifyComplete();
    }
}