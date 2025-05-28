package fr.hoenheimsports.trainingservice.dto.request;

import fr.hoenheimsports.trainingservice.dto.validator.annotation.IsRole;

public record AddCoachInTeamDTORequest(long coachId, @IsRole String role) {
}
