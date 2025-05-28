package fr.hoenheimsports.trainingservice.dto.request;

import fr.hoenheimsports.trainingservice.dto.validator.annotation.UniqueTeam;
import fr.hoenheimsports.trainingservice.model.Category;
import fr.hoenheimsports.trainingservice.model.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

@UniqueTeam(message = "L'équipe existe déjà")
public record TeamDTOCreateRequest(@NotNull(message = "Le genre est obligatoire")
                                   Gender gender,
                                   @NotNull(message = "La catégorie est obligatoire")
                                   Category category,
                                   @Positive(message = "Le numéro d'équipe doit être supérieur ou égal à 1")
                                   int teamNumber
) implements Serializable {
}