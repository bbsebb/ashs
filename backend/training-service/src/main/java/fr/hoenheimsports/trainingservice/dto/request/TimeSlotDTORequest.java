package fr.hoenheimsports.trainingservice.dto.request;

import fr.hoenheimsports.trainingservice.dto.validator.annotation.StartTimeBeforeEndTime;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.TimeSlot}
 */
@Builder
@StartTimeBeforeEndTime
public record TimeSlotDTORequest(
        @NotNull(message = "Le jour d'un entrainement est obligatoire")
        DayOfWeek dayOfWeek,
        @NotNull(message = "L'heure de début d'un entrainement est obligatoire")
        LocalTime startTime,
        @NotNull(message = "L'heure de fin d'un entrainement est obligatoire")
        LocalTime endTime
) implements Serializable {
}