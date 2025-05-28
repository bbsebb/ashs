package fr.hoenheimsports.trainingservice.dto.request;

import fr.hoenheimsports.trainingservice.dto.validator.annotation.IsRole;

public record RoleCoachDTORequest(@IsRole String role) {
}
