package fr.hoenheimsports.trainingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.hoenheimsports.trainingservice.model.Category;
import fr.hoenheimsports.trainingservice.model.Gender;
import lombok.Builder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.Team}
 */
@Relation(collectionRelation = "teams")
@Builder
public record TeamDTOResponse(
        Long id,
        Gender gender,
        Category category,
        int teamNumber
) implements Serializable {}