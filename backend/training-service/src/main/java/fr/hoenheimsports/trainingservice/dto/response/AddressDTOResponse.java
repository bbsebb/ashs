package fr.hoenheimsports.trainingservice.dto.response;

import fr.hoenheimsports.trainingservice.model.Address;
import lombok.Builder;

import java.io.Serializable;

/**
 * AddressDTOResponse is a Data Transfer Object (DTO) representing the response structure for an Address entity ({@link Address}).
 * This class provides a simplified representation of an address, including details such as street, city, postal code, and country.
 *
 * It is mainly used to transfer address-related data between different processes or layers, such as the service and presentation layers.
 *
 * AddressDTOResponse implements {@code Serializable} to support serialization and deserialization processes.
 * It is commonly utilized in conjunction with other DTOs, like HallDTOResponse, to represent complex object hierarchies.
 *
 * The class is annotated with Lombok's {@code @Builder}, enabling easy creation of AddressDTOResponse objects using a fluent API.
 */

@Builder
public record AddressDTOResponse(String street, String city, String postalCode,

                                 String country) implements Serializable {
}