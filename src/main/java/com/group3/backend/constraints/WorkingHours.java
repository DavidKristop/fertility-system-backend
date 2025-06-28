package com.group3.backend.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WorkingHoursValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkingHours {
    String message() default "Appointment time must be between 8:00 AM and 6:00 PM on weekdays";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
