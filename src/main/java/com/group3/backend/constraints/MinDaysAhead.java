package com.group3.backend.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MinDaysAheadValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MinDaysAhead {
    int value() default 3;
    String message() default "Appointment must be at least 3 days from now";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
