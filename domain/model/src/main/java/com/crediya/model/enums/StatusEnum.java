package com.crediya.model.enums;

public enum StatusEnum {
    EN_PROCESO,  
    APROBADO,    
    RECHAZADO;     

    // Método opcional para convertir desde String
    public static StatusEnum fromString(String value) {
        if (value == null) return null;
        return switch (value.toUpperCase()) {
            case "EN_PROCESO" -> EN_PROCESO;
            case "APROBADO" -> APROBADO;
            case "RECHAZADO" -> RECHAZADO;
            default -> throw new IllegalArgumentException("Valor inválido para Status: " + value);
        };
    }
}
