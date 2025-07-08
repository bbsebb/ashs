package fr.hoenheimsports.trainingservice.dto.response;

import fr.hoenheimsports.trainingservice.model.Hall;
import lombok.Builder;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;

/**
 * HallDTOResponse is a Data Transfer Object (DTO) representing the response structure for a Hall entity ({@link Hall}).
 * This class provides a simplified representation of halls, including their identifier, name, and address details.
 * It is primarily used to transfer data between processes or layers such as the service and presentation layer.
 * <p>
 * It implements {@code Serializable} to allow for serialization and deserialization.
 * The address field is represented by {@code AddressDTOResponse}, offering details like street, city, postal code, and country.
 * <p>
 * The class is annotated with {@code @Relation(collectionRelation = "halls")} to define its relation in HATEOAS-compliant APIs.
 * It uses Lombok's {@code @Builder} to enable streamlined object creation.
 */
@Relation(collectionRelation = "halls")
@Builder
public record HallDTOResponse(Long id, String name, AddressDTOResponse address) implements Serializable {
}