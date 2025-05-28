package fr.hoenheimsports.trainingservice.mapper;

import fr.hoenheimsports.trainingservice.dto.request.TeamDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.request.TeamDTOUpdateRequest;
import fr.hoenheimsports.trainingservice.dto.response.TeamDTOResponse;
import fr.hoenheimsports.trainingservice.model.Team;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeamMapper {
    Team toEntity(TeamDTOUpdateRequest teamDTOUpdateRequest);

    Team toEntity(TeamDTOCreateRequest teamDTOCreateRequest);

    TeamDTOUpdateRequest toDto1(Team team);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Team partialUpdate(TeamDTOUpdateRequest teamDTOUpdateRequest, @MappingTarget Team team);


    Team toEntity(TeamDTOResponse teamDTOResponse);

    TeamDTOResponse toDto(Team team);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Team partialUpdate(TeamDTOResponse teamDTOResponse, @MappingTarget Team team);
}