package fr.hoenheimsports.facebookservice.mapper;

import fr.hoenheimsports.facebookservice.controller.dto.FeedDTOResponse;
import fr.hoenheimsports.facebookservice.feignClient.dto.FeedDTO;
import fr.hoenheimsports.facebookservice.model.FeedEntity;
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