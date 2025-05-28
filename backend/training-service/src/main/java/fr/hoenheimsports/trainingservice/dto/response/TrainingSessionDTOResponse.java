package fr.hoenheimsports.trainingservice.dto.response;

import lombok.Builder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.TrainingSession}
 */
@Relation(collectionRelation = "trainingSessions")
@Builder
public record TrainingSessionDTOResponse(Long id, TimeSlotDTOResponse timeSlot,
                                         EntityModel<HallDTOResponse> hall) implements Serializable {
    public TrainingSessionDTOResponse withAdditionalHallEntityModel(@NonNull EntityModel<HallDTOResponse> hallModel) {
        if (this.hall != null) {
            throw new IllegalStateException("Cannot add additional ressources to a TeamDTOResponse that already has ressources.");
        }
        Assert.notNull(hallModel, "additionalLinks must not be null");

        return new TrainingSessionDTOResponse(id, timeSlot, hallModel);
    }
}