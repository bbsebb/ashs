package fr.hoenheimsports.trainingservice.dto.validator;

import fr.hoenheimsports.trainingservice.dto.validator.annotation.IsRole;
import fr.hoenheimsports.trainingservice.model.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RoleValidator  implements ConstraintValidator<IsRole, String>
{
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            Role.valueOf(value); // Vérifie si la chaîne correspond à une valeur de l'enum
            return true; // Si aucun problème, elle est dans l'enum
        } catch (IllegalArgumentException e) {
            // Désactiver le message par défaut
            context.disableDefaultConstraintViolation();

            String roles = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.joining(" "));
            // Ajouter un nouveau message personnalisé
            context.buildConstraintViolationWithTemplate("Valeur pour le champs doit être role valide")
                    .addConstraintViolation();

            return false; // Sinon, ce n'est pas une valeur valide
        }
    }
}
