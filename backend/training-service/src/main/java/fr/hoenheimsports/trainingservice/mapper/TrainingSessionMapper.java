package fr.hoenheimsports.trainingservice.mapper;

import fr.hoenheimsports.trainingservice.dto.request.TrainingSessionDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.TrainingSessionDTOResponse;
import fr.hoenheimsports.trainingservice.model.TrainingSession;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {TimeSlotMapper.class, HallMapper.class})
public interface TrainingSessionMapper {
    TrainingSession toEntity(TrainingSessionDTOResponse trainingSessionDTOResponse);

    @Mapping(target = "hall", ignore = true)
        //Le mappage de hall se fait dans l'assembler puisque je renvoie un entityModel de Hall
    TrainingSessionDTOResponse toDto(TrainingSession trainingSession);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TrainingSession partialUpdate(TrainingSessionDTOResponse trainingSessionDTOResponse, @MappingTarget TrainingSession trainingSession);

    TrainingSession toEntity(TrainingSessionDTORequest trainingSessionDTORequest);

    TrainingSessionDTORequest toDto1(TrainingSession trainingSession);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TrainingSession partialUpdate(TrainingSessionDTORequest trainingSessionDTORequest, @MappingTarget TrainingSession trainingSession);
}