package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
import fr.hoenheimsports.trainingservice.model.Coach;
import org.springframework.hateoas.EntityModel;

public interface CoachAssembler extends BaseAssembler<Coach, EntityModel<CoachDTOResponse>> {
}
