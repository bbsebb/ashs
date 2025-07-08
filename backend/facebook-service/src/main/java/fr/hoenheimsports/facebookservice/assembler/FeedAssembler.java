package fr.hoenheimsports.facebookservice.assembler;

import fr.hoenheimsports.facebookservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.facebookservice.model.FeedEntity;
import org.springframework.hateoas.EntityModel;

public interface FeedAssembler extends BaseAssembler<FeedEntity, EntityModel<FeedDTOResponse>> {
}
