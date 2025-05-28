package fr.hoenheimsports.trainingservice.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.TimeSlot}
 */
@Builder
public record TimeSlotDTOResponse(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) implements Serializable {
}