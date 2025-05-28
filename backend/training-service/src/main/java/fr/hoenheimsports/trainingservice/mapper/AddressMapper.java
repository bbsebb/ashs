package fr.hoenheimsports.trainingservice.mapper;

import fr.hoenheimsports.trainingservice.dto.response.AddressDTOResponse;
import fr.hoenheimsports.trainingservice.model.Address;
import org.mapstruct.*;

/**
 * AddressMapper is a MapStruct interface designed to provide mapping functionality between Address entities
 * and their corresponding Data Transfer Objects (DTOs), primarily AddressDTOResponse.
 *
 * This mapper defines the methods required for converting between Address and AddressDTOResponse objects,
 * as well as partially updating Address entity instances based on AddressDTOResponse data.
 *
 * Configuration:
 * - `@Mapper`: Specifies that this interface is a MapStruct mapper.
 * - `unmappedTargetPolicy`: Set to `ReportingPolicy.IGNORE`, ensuring unmapped target properties are ignored without generating warnings.
 * - `componentModel`: Configured to `SPRING`, enabling this mapper to be automatically detected and managed as a Spring Bean.
 *
 * Main Methods:
 * - `toEntity(AddressDTOResponse addressDtoResponse)`: Maps an instance of AddressDTOResponse to an Address entity.
 * - `toDto(Address address)`: Maps an Address entity to its corresponding AddressDTOResponse representation.
 * - `partialUpdate(AddressDTOResponse addressDtoResponse, Address address)`: Partially updates fields of an Address entity
 *   with non-null values from an AddressDTOResponse instance. Null-value properties in the source object are ignored.
 *
 * Usage:
 * Typically used in service layers or controllers to seamlessly convert between internal Address entity objects
 * and external DTO representations or to perform partial updates for existing entities.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    Address toEntity(AddressDTOResponse addressDtoResponse);

    AddressDTOResponse toDto(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Address partialUpdate(AddressDTOResponse addressDtoResponse, @MappingTarget Address address);

}