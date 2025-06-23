package fr.hoenheimsports.instagramservice.mapper;

import fr.hoenheimsports.instagramservice.controller.dto.TargetDTOResponse;
import fr.hoenheimsports.instagramservice.feignClient.dto.TargetDTO;
import fr.hoenheimsports.instagramservice.model.TargetEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TargetEmbeddableMapper {
    TargetEmbeddable toEntity(TargetDTO targetDTO);

    TargetDTOResponse toDto(TargetEmbeddable targetEmbeddable);


}