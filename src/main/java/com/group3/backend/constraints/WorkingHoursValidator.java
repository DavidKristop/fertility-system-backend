package com.group3.backend.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class WorkingHoursValidator implements ConstraintValidator<WorkingHours, OffsetDateTime> {
    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final LocalTime END_TIME = LocalTime.of(18, 0);

    @Override
    public boolean isValid(OffsetDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null cases
        }

        DayOfWeek dayOfWeek = value.getDayOfWeek();
        LocalTime time = value.toLocalTime();

        // Check if it's a weekday
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // Check if it's within working hours
        return !time.isBefore(START_TIME) && !time.isAfter(END_TIME);
    }
}
