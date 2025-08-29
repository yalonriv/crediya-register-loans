package com.crediya.model.enums;

import com.crediya.model.exceptions.InvalidStatusException;
import reactor.core.publisher.Mono;

public enum StatusEnum {
    EN_PROCESO,
    APROBADO,
    RECHAZADO;

    public static Mono<StatusEnum> fromString(String value) {
        if (value == null) {
            return Mono.error(new InvalidStatusException("Status cannot be null"));
        }

        try {
            return Mono.just(StatusEnum.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Mono.error(new InvalidStatusException(value));
        }
    }

}