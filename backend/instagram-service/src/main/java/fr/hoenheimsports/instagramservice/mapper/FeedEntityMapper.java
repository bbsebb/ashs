package fr.hoenheimsports.instagramservice.mapper;

import fr.hoenheimsports.instagramservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.instagramservice.feignClient.dto.FeedDTO;
import fr.hoenheimsports.instagramservice.model.FeedEntity;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {AttachmentEntityMapper.class, DateMapper.class})
public interface FeedEntityMapper {
    @Mapping(source = "id", target = "graphApiId")
    FeedEntity toEntity(FeedDTO feedDTO);

    @AfterMapping
    default void linkAttachments(@MappingTarget FeedEntity feedEntity) {
        feedEntity.getAttachments().forEach(attachment -> attachment.setFeed(feedEntity));
    }


    FeedDTOResponse toDto(FeedEntity feedEntity);


}