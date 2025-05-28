package fr.hoenheimsports.trainingservice.mapper;

import fr.hoenheimsports.trainingservice.dto.response.RoleCoachDTOResponse;
import fr.hoenheimsports.trainingservice.model.RoleCoach;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {CoachMapper.class})
public interface RoleCoachMapper {
    RoleCoach toEntity(RoleCoachDTOResponse roleCoachDTOResponse);

    @Mapping(target = "coach", ignore = true)
    @Mapping(target = "team", ignore = true)
    RoleCoachDTOResponse toDto(RoleCoach roleCoach);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RoleCoach partialUpdate(RoleCoachDTOResponse roleCoachDTOResponse, @MappingTarget RoleCoach roleCoach);

    RoleCoach toEntity(RoleCoach roleCoach);

}