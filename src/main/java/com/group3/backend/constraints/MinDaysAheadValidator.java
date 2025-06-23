package com.group3.backend.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.OffsetDateTime;

public class MinDaysAheadValidator implements ConstraintValidator<MinDaysAhead, OffsetDateTime> {
    private int days;

    @Override
    public void initialize(MinDaysAhead constraintAnnotation) {
        this.days = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(OffsetDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null cases
        }

        OffsetDateTime now = OffsetDateTime.now();
        return value.isAfter(now.plusDays(days));
    }
}
