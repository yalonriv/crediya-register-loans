package com.crediya.r2dbc.entities;

import com.crediya.model.enums.StatusEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("loan")
public class LoanEntity {
    @Id
    private Long id;
    @Column("dni_client")
    private Long dniClient;
    private Double amount;
    @Column("dead_line")
    private LocalDate deadLine;
    @Column("loan_type_id")
    private Long loanTypeId;
    private String status;

    public StatusEnum getStatusAsEnum() {
        return StatusEnum.fromString(this.status);
    }

    public void setStatusFromEnum(StatusEnum status) {
        this.status = status != null ? status.name() : null;
    }
}
