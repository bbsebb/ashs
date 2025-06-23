package fr.hoenheimsports.instagramservice.mapper;

import fr.hoenheimsports.instagramservice.controller.dto.ImageDTOResponse;
import fr.hoenheimsports.instagramservice.feignClient.dto.ImageDTO;
import fr.hoenheimsports.instagramservice.model.ImageEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageEmbeddableMapper {
    ImageEmbeddable toEntity(ImageDTO imageDTO);

    ImageDTOResponse toDto(ImageEmbeddable imageEmbeddable);


}