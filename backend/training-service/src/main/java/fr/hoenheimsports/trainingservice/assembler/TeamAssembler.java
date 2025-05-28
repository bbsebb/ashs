package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.dto.response.TeamDTOResponse;
import fr.hoenheimsports.trainingservice.model.Team;
import org.springframework.hateoas.EntityModel;

/**
 * Interface for assemblers that convert Team entities to HATEOAS-compliant representations.
 * This interface handles the creation of links and affordances for Team resources.
 */
public interface TeamAssembler extends BaseAssembler<Team, EntityModel<TeamDTOResponse>> {
}
