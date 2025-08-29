package com.crediya.usecase.loan;

import com.crediya.model.enums.StatusEnum;
import com.crediya.model.exceptions.InvalidStatusException;
import com.crediya.model.exceptions.LoanTypeNotFoundException;
import com.crediya.model.loan.Loan;
import com.crediya.model.loan.gateways.LoanRepository;
import com.crediya.model.loantype.LoanType;
import com.crediya.model.loantype.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @InjectMocks
    private LoanUseCase loanUseCase;

    private Loan validLoan;
    private LoanType loanType;

    @BeforeEach
    void setUp() {
        validLoan = new Loan();
        validLoan.setId(1L);
        validLoan.setDniClient(12345678L);
        validLoan.setAmount(5000.0);
        validLoan.setDeadLine(LocalDate.now().plusMonths(12));
        validLoan.setLoanTypeId(1L);
        validLoan.setStatus(StatusEnum.APROBADO);
        loanType = new LoanType();
        loanType.setId(1L);
        loanType.setTypeName("Préstamo Personal");
        // Set other necessary fields for LoanType
    }

    @Test
    void createLoan_WithValidStatusAPROBADO_ShouldSaveLoan() {
        // Arrange
        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(Mono.just(validLoan));

        // Act & Assert
        StepVerifier.create(loanUseCase.createLoan(validLoan))
                .expectNext(validLoan)
                .verifyComplete();

        verify(loanRepository, times(1)).saveLoan(validLoan);
    }

    @Test
    void createLoan_WithValidStatusEN_PROCESO_ShouldSaveLoan() {
        // Arrange
        Loan loanEnProceso = new Loan();
        loanEnProceso.setId(2L);
        loanEnProceso.setDniClient(87654321L);
        loanEnProceso.setAmount(3000.0);
        loanEnProceso.setDeadLine(LocalDate.now().plusMonths(6));
        loanEnProceso.setLoanTypeId(1L);
        loanEnProceso.setStatus(StatusEnum.EN_PROCESO);

        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(Mono.just(loanEnProceso));

        // Act & Assert
        StepVerifier.create(loanUseCase.createLoan(loanEnProceso))
                .expectNext(loanEnProceso)
                .verifyComplete();

        verify(loanRepository, times(1)).saveLoan(loanEnProceso);
    }

    @Test
    void createLoan_WithValidStatusRECHAZADO_ShouldSaveLoan() {
        // Arrange
        Loan loanRechazado = new Loan();
        loanRechazado.setId(3L);
        loanRechazado.setDniClient(11223344L);
        loanRechazado.setAmount(1000.0);
        loanRechazado.setDeadLine(LocalDate.now().plusMonths(3));
        loanRechazado.setLoanTypeId(1L);
        loanRechazado.setStatus(StatusEnum.RECHAZADO);

        when(loanRepository.saveLoan(any(Loan.class))).thenReturn(Mono.just(loanRechazado));

        // Act & Assert
        StepVerifier.create(loanUseCase.createLoan(loanRechazado))
                .expectNext(loanRechazado)
                .verifyComplete();

        verify(loanRepository, times(1)).saveLoan(loanRechazado);
    }

    @Test
    void createLoan_WithNullStatus_ShouldThrowInvalidStatusException() {
        // Arrange - Usa constructor en lugar de builder
        Loan loanWithNullStatus = new Loan();
        loanWithNullStatus.setStatus(null);

        // Act & Assert
        StepVerifier.create(loanUseCase.createLoan(loanWithNullStatus))
                .expectError(InvalidStatusException.class)
                .verify();

        verifyNoInteractions(loanRepository);
    }

    @Test
    void updateLoan_ShouldCallRepositoryEditLoan() {
        // Arrange
        when(loanRepository.editLoan(any(Loan.class))).thenReturn(Mono.just(validLoan));

        // Act & Assert
        StepVerifier.create(loanUseCase.updateLoan(validLoan))
                .expectNext(validLoan)
                .verifyComplete();

        verify(loanRepository, times(1)).editLoan(validLoan);
    }

    @Test
    void findById_WithExistingLoanType_ShouldReturnLoanType() {
        // Arrange
        Long loanTypeId = 1L;
        when(loanTypeRepository.findById(loanTypeId)).thenReturn(Mono.just(loanType));

        // Act & Assert
        StepVerifier.create(loanUseCase.findById(loanTypeId))
                .expectNext(loanType)
                .verifyComplete();

        verify(loanTypeRepository, times(1)).findById(loanTypeId);
    }

    @Test
    void findById_WithNonExistingLoanType_ShouldThrowLoanTypeNotFoundException() {
        // Arrange
        Long nonExistingId = 999L;
        when(loanTypeRepository.findById(nonExistingId)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(loanUseCase.findById(nonExistingId))
                .expectErrorMatches(throwable ->
                        throwable instanceof LoanTypeNotFoundException &&
                                throwable.getMessage().contains(nonExistingId.toString()))
                .verify();

        verify(loanTypeRepository, times(1)).findById(nonExistingId);
    }
}