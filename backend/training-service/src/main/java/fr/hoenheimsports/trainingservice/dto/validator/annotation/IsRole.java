package fr.hoenheimsports.trainingservice.dto.validator.annotation;

import fr.hoenheimsports.trainingservice.dto.validator.RoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RoleValidator.class)
@Target({ElementType.FIELD}) // Appliqué sur un champs
@Retention(RetentionPolicy.RUNTIME)
public @interface IsRole {
    String message() default "Valeur pour le champs doit être role valide ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
