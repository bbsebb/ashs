package fr.hoenheimsports.trainingservice.dto.request;

import jakarta.validation.Valid;

public record AddTrainingSessionInTeamDTORequest(Long hallId, @Valid TrainingSessionDTORequest trainingSessionDTORequest) {
}
