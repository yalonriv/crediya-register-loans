package com.crediya.api.dtos;

public record ErrorResponse(String code, String message) {
    // Ejemplo: {"code": "CLIENTE_NO_EXISTE", "message": "El cliente no existe"}
}
