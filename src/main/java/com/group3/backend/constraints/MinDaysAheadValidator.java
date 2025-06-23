package com.group3.backend.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MinDaysAheadValidator implements ConstraintValidator<MinDaysAhead, Timestamp> {
    private int days;

    @Override
    public void initialize(MinDaysAhead constraintAnnotation) {
        this.days = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Timestamp value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null cases
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime valueLocalDateTime = value.toLocalDateTime();
        
        if (valueLocalDateTime.isBefore(now.plusDays(days))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Appointment must be at least " + days + " days from now").addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
