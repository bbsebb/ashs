package fr.hoenheimsports.instagramservice.mapper;

import fr.hoenheimsports.instagramservice.controller.dto.PostDTOResponse;
import fr.hoenheimsports.instagramservice.feignClient.dto.PostDTO;
import fr.hoenheimsports.instagramservice.model.PostEntity;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {AttachmentEntityMapper.class, DateMapper.class})
public interface PostEntityMapper {
    @Mapping(source = "id", target = "graphApiId")
    PostEntity toEntity(PostDTO postDTO);

    @AfterMapping
    default void linkAttachments(@MappingTarget PostEntity postEntity) {
        postEntity.getAttachments().forEach(attachment -> attachment.setPost(postEntity));
    }


    PostDTOResponse toDto(PostEntity postEntity);


}