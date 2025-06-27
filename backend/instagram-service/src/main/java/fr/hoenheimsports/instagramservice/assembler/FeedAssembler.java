package fr.hoenheimsports.instagramservice.assembler;

import fr.hoenheimsports.instagramservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.instagramservice.model.FeedEntity;
import org.springframework.hateoas.EntityModel;

public interface FeedAssembler extends BaseAssembler<FeedEntity, EntityModel<FeedDTOResponse>> {
}
