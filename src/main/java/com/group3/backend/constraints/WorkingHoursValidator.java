package com.group3.backend.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class WorkingHoursValidator implements ConstraintValidator<WorkingHours, LocalDateTime> {
    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final LocalTime END_TIME = LocalTime.of(18, 0);

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; 
        }

        DayOfWeek dayOfWeek = value.getDayOfWeek();
        LocalTime time = value.toLocalTime();

        // Check if it's a weekday
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Appointments are not allowed on weekends").addConstraintViolation();
            return false;
        }

        // Check if it's within working hours
        if (time.isBefore(START_TIME)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Appointments must be after 8:00 AM").addConstraintViolation();
            return false;
        }
        
        if (time.isAfter(END_TIME)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Appointments must be before 6:00 PM").addConstraintViolation();
            return false;
        }

        return true;
    }
}
