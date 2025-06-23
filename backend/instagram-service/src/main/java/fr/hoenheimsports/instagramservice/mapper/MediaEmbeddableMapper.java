package fr.hoenheimsports.instagramservice.mapper;

import fr.hoenheimsports.instagramservice.controller.dto.MediaDTOResponse;
import fr.hoenheimsports.instagramservice.feignClient.dto.MediaDTO;
import fr.hoenheimsports.instagramservice.model.MediaEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ImageEmbeddableMapper.class})
public interface MediaEmbeddableMapper {
    MediaEmbeddable toEntity(MediaDTO mediaDTO);

    MediaDTOResponse toDto(MediaEmbeddable mediaEmbeddable);


}