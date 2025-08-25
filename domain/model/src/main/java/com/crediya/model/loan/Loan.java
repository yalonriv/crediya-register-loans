package com.crediya.model.loan;
import com.crediya.model.enums.StatusEnum;
import com.crediya.model.loantype.LoanType;
import lombok.*;

import java.time.LocalDate;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Loan {
    private Long id;
    private Long dniClient;
    private Double amount;
    private LocalDate deadLine;
    private Long loanTypeId;
    private StatusEnum status;
}
