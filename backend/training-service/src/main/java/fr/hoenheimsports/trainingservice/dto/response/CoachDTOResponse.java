package fr.hoenheimsports.trainingservice.dto.response;

import lombok.Builder;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.Coach}
 */
@Relation(collectionRelation = "coaches")
@Builder
public record CoachDTOResponse(Long id, String name, String surname, String email, String phone) implements Serializable {
  }