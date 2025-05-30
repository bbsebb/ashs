package fr.hoenheimsports.trainingservice.dto.validator.annotation;

import fr.hoenheimsports.trainingservice.dto.validator.UniqueHallValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueHallValidator.class)
@Target(ElementType.TYPE) // Appliqué sur la classe
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueHall {
    String message() default "Cette salle existe déjà";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

