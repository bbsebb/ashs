package fr.hoenheimsports.instagramservice.mapper;

import fr.hoenheimsports.instagramservice.controller.dto.AttachmentDTOResponse;
import fr.hoenheimsports.instagramservice.feignClient.dto.AttachmentDTO;
import fr.hoenheimsports.instagramservice.feignClient.dto.AttachmentsDTO;
import fr.hoenheimsports.instagramservice.model.AttachmentEntity;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {SubAttachmentEntityMapper.class, MediaEmbeddableMapper.class, FeedEntityMapper.class})
public interface AttachmentEntityMapper {
    AttachmentEntity toEntity(AttachmentDTO attachmentDTO);

    @AfterMapping
    default void linkSubAttachments(@MappingTarget AttachmentEntity attachmentEntity) {
        attachmentEntity.getSubAttachments().forEach(subAttachment -> subAttachment.setAttachment(attachmentEntity));
    }

    default List<AttachmentEntity> map(AttachmentsDTO dto) {
        if (dto == null || dto.data() == null) {
            return new ArrayList<>();
        }
        return dto.data().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    AttachmentDTOResponse toDto(AttachmentEntity attachmentEntity);


}