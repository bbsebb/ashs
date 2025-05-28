package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import org.springframework.hateoas.EntityModel;

public interface TrainingSessionAssembler extends BaseAssembler<TrainingSession, EntityModel<TrainingSessionDTOResponse>>{
}
