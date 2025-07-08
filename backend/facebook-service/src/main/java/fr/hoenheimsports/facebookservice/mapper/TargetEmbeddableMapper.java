package fr.hoenheimsports.facebookservice.mapper;

import fr.hoenheimsports.facebookservice.controller.dto.TargetDTOResponse;
import fr.hoenheimsports.facebookservice.feignClient.dto.TargetDTO;
import fr.hoenheimsports.facebookservice.model.TargetEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TargetEmbeddableMapper {
    TargetEmbeddable toEntity(TargetDTO targetDTO);

    TargetDTOResponse toDto(TargetEmbeddable targetEmbeddable);


}