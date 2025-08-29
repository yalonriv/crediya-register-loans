package com.crediya.api.mapper;

import com.crediya.api.dtos.CreateLoanDTO;
import com.crediya.api.dtos.EditLoanDTO;
import com.crediya.model.loan.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanDTOMapper {
    Loan toModel(CreateLoanDTO createUserDTO);
}
