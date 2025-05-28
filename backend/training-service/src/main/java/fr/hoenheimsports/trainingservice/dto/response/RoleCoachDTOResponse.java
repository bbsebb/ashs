package fr.hoenheimsports.trainingservice.dto.response;

import fr.hoenheimsports.trainingservice.model.Role;
import lombok.Builder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.RoleCoach}
 */
@Relation(collectionRelation = "roleCoaches")
@Builder
public record RoleCoachDTOResponse(Long id, Role role, EntityModel<CoachDTOResponse> coach,
                                   EntityModel<TeamDTOResponse> team) implements Serializable {


}