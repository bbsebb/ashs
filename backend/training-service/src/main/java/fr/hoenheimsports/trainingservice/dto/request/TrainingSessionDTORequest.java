package fr.hoenheimsports.trainingservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.TrainingSession}
 */
@Builder
public record TrainingSessionDTORequest(@NotNull(message = "Le créneau d'entrainement est obligatoire")
                                        TimeSlotDTORequest timeSlot) implements Serializable {
}