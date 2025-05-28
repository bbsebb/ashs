package fr.hoenheimsports.trainingservice.mapper;

import fr.hoenheimsports.trainingservice.dto.request.TimeSlotDTORequest;
import fr.hoenheimsports.trainingservice.dto.response.TimeSlotDTOResponse;
import fr.hoenheimsports.trainingservice.model.TimeSlot;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TimeSlotMapper {
    TimeSlot toEntity(TimeSlotDTOResponse timeSlotDTOResponse);

    TimeSlotDTOResponse toDto(TimeSlot timeSlot);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TimeSlot partialUpdate(TimeSlotDTOResponse timeSlotDTOResponse, @MappingTarget TimeSlot timeSlot);

    TimeSlot toEntity(TimeSlotDTORequest timeSlotDTORequest);

    TimeSlotDTORequest toDto1(TimeSlot timeSlot);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TimeSlot partialUpdate(TimeSlotDTORequest timeSlotDTORequest, @MappingTarget TimeSlot timeSlot);
}