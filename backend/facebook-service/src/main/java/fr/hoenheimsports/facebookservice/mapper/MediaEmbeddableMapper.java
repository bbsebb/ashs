package fr.hoenheimsports.facebookservice.mapper;

import fr.hoenheimsports.facebookservice.controller.dto.MediaDTOResponse;
import fr.hoenheimsports.facebookservice.feignClient.dto.MediaDTO;
import fr.hoenheimsports.facebookservice.model.MediaEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ImageEmbeddableMapper.class})
public interface MediaEmbeddableMapper {
    MediaEmbeddable toEntity(MediaDTO mediaDTO);

    MediaDTOResponse toDto(MediaEmbeddable mediaEmbeddable);


}