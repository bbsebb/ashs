package fr.hoenheimsports.trainingservice.assembler;

import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.model.Hall;
import org.springframework.hateoas.EntityModel;

public interface HallAssembler extends BaseAssembler<Hall, EntityModel<HallDTOResponse>> {
}