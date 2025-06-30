package com.group3.backend.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class MinDaysAheadValidator implements ConstraintValidator<MinDaysAhead, LocalDateTime> {
    private int days;

    @Override
    public void initialize(MinDaysAhead constraintAnnotation) {
        this.days = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        
        if (value.isBefore(now.plusDays(days))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Appointment must be at least " + days + " days from now").addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
