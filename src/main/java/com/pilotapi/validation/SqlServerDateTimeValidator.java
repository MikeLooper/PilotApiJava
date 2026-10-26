package com.pilotapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class SqlServerDateTimeValidator implements ConstraintValidator<SqlServerDateTime, LocalDateTime> {

    private static final LocalDateTime MINIMUM = LocalDateTime.of(1753, 1, 1, 0, 0);

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(MINIMUM);
    }
}
