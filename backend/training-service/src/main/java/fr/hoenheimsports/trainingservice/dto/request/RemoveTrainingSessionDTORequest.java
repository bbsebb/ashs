package fr.hoenheimsports.trainingservice.dto.request;

import jakarta.validation.constraints.Positive;

public record RemoveTrainingSessionDTORequest(@Positive long trainingSessionId) {
}
