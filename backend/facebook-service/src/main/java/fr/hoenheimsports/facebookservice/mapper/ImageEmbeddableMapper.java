package fr.hoenheimsports.facebookservice.mapper;

import fr.hoenheimsports.facebookservice.controller.dto.ImageDTOResponse;
import fr.hoenheimsports.facebookservice.feignClient.dto.ImageDTO;
import fr.hoenheimsports.facebookservice.model.ImageEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageEmbeddableMapper {
    ImageEmbeddable toEntity(ImageDTO imageDTO);

    ImageDTOResponse toDto(ImageEmbeddable imageEmbeddable);


}