package fr.hoenheimsports.trainingservice.mapper;

import fr.hoenheimsports.trainingservice.dto.request.HallDTOCreateRequest;
import fr.hoenheimsports.trainingservice.dto.request.HallDTOUpdateRequest;
import fr.hoenheimsports.trainingservice.dto.response.HallDTOResponse;
import fr.hoenheimsports.trainingservice.model.Hall;
import org.mapstruct.*;

/**
 * HallMapper is a MapStruct interface designed to provide mapping functionality
 * between Hall entities and their corresponding Data Transfer Objects (DTOs),
 * specifically HallDTORequest and HallDTOResponse.
 * <p>
 * This mapper facilitates seamless data transformation between the domain model
 * (Hall entity) and DTOs used within the application, ensuring consistency,
 * reusability, and reduced boilerplate code.
 * <p>
 * Configuration:
 * - `@Mapper`: Declares this interface as a MapStruct mapper.
 * - `unmappedTargetPolicy`: Set to `ReportingPolicy.IGNORE`, suppressing warnings
 * when source properties do not have matching target properties.
 * - `componentModel`: Configured as `SPRING` to enable the mapper to be managed
 * as a Spring bean.
 * - `uses`: Specifies that this mapper depends on `AddressMapper` for nested
 * mapping of Address-related fields.
 * <p>
 * Main Methods:
 * - `toDto(Hall hall)`: Converts a Hall entity into its corresponding
 * HallDTOResponse representation.
 * - `toEntity(HallDTORequest hallDtoRequest)`: Transforms a HallDTORequest
 * instance into a Hall entity.
 * - `partialUpdate(HallDTORequest hallDtoRequest, @MappingTarget Hall hall)`:
 * Partially updates an existing Hall entity with the non-null properties
 * from a HallDTORequest object. Null properties in the source object
 * are ignored during the update.
 * <p>
 * Usage Context:
 * This mapper is useful in scenarios where Hall objects need to be converted
 * between different layers of the application, such as from the persistence
 * layer (entity) to the presentation layer (DTO) or vice versa. Additionally,
 * the partial update method supports efficient updates of existing entities.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {AddressMapper.class})
public interface HallMapper {

    HallDTOResponse toDto(Hall hall);

    Hall toEntity(HallDTOCreateRequest hallDtoCreateRequest);

    Hall toEntity(HallDTOUpdateRequest hallDtoUpdateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Hall partialUpdate(HallDTOCreateRequest hallDtoCreateRequest, @MappingTarget Hall hall);

    Hall toEntity(HallDTOResponse hallDTOResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Hall partialUpdate(HallDTOResponse hallDTOResponse, @MappingTarget Hall hall);
}