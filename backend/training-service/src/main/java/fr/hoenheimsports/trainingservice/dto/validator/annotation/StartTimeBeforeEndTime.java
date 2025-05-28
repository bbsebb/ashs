package fr.hoenheimsports.trainingservice.dto.validator.annotation;

import fr.hoenheimsports.trainingservice.dto.validator.StartTimeBeforeEndTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StartTimeBeforeEndTimeValidator.class)
@Target(ElementType.TYPE) // Appliqué sur la classe
@Retention(RetentionPolicy.RUNTIME)
public @interface StartTimeBeforeEndTime {
    String message() default "La date de début doit être avant la date de fin";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
