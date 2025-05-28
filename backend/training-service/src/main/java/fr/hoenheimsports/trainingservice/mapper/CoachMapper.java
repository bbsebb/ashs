package fr.hoenheimsports.trainingservice.mapper;

import fr.hoenheimsports.trainingservice.dto.request.CoachDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.CoachDTOResponse;
import fr.hoenheimsports.trainingservice.model.Coach;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CoachMapper {
    Coach toEntity(CoachDTOResponse coachDTOResponse);



    CoachDTOResponse toDto(Coach coach);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Coach partialUpdate(CoachDTOResponse coachDTOResponse, @MappingTarget Coach coach);

    Coach toEntity(CoachDTORequest coachDTORequest);


    CoachDTORequest toDto1(Coach coach);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Coach partialUpdate(CoachDTORequest coachDTORequest, @MappingTarget Coach coach);
}