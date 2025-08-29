package com.crediya.api;

import com.crediya.api.dtos.CreateLoanDTO;
import com.crediya.api.dtos.ErrorResponse;
import com.crediya.model.loan.Loan;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
@Tag(name = "Prestamos", description = "Operaciones sobre préstamos")
public class RouterRest {

    private final Handler handler;

    public RouterRest(Handler handler) {
        this.handler = handler;
    }

    @Bean
    @RouterOperations({@RouterOperation(path = "/loan", method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "createLoan", operation = @Operation(operationId = "createLoan", summary = "Crear un préstamo", description = "Crea un nuevo préstamo con el DNI del cliente, el tipo de préstamo y el monto solicitado.", tags = {"Prestamos"}, requestBody = @RequestBody(required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateLoanDTO.class), examples = {@ExampleObject(name = "Ejemplo request", value = """
            {
              "dniClient": 12345678,
              "loanTypeId": 2,
              "amount": 5000,
              "deadLine": "2025-12-31",
              "status": "APROBADO"
            }
            """)})), responses = {@ApiResponse(responseCode = "200", description = "Préstamo creado correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Loan.class), examples = {@ExampleObject(name = "Ejemplo response", value = """
                  {
               "dniClient": 12345678,
              "loanTypeId": 2,
              "amount": 5000,
              "deadLine": "2025-12-31",
              "status": "APROBADO"
                  }
            """)})), @ApiResponse(responseCode = "400", description = "Error en la petición", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject(name = "Error", value = """
            {
              "code": "DATOS_INVALIDOS",
              "message": "El DNI es obligatorio"
            }
            """)})), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject(name = "Error Interno", value = """
            {
              "code": "ERROR_INTERNO",
              "message": "Error del servidor"
            }
            """)}))}))})
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route().POST("/loan", handler::createLoan).build();
    }
}
