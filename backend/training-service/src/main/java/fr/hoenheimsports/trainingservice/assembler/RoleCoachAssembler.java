package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import org.springframework.hateoas.EntityModel;

public interface RoleCoachAssembler extends BaseAssembler<RoleCoach, EntityModel<RoleCoachDTOResponse>> {
}
