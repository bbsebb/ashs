package fr.hoenheimsports.trainingservice.dto.request;

import fr.hoenheimsports.trainingservice.dto.validator.annotation.UniqueHall;
import fr.hoenheimsports.trainingservice.model.Hall;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

/**
 * HallDTORequest is a Data Transfer Object (DTO) used to represent the input data for a hall entity ({@link Hall}).
 * This class is designed to encapsulate the information needed when creating or updating a hall,
 * ensuring data validation and consistency.
 * <p>
 * The HallDTORequest includes the following fields:
 * - name: The name of the hall. It is mandatory and must not exceed 50 characters.
 * - address: An AddressDTORequest object representing the address details associated with the hall.
 * <p>
 * This class uses validation annotations to enforce constraints:
 * - @NotBlank ensures the name is not empty or null.
 * - @Size adds a restriction on the maximum permitted character length for the hall's name.
 * <p>
 * The AddressDTORequest field represents the address of the hall, which includes details such as
 * street, city, postal code, and country. This ensures proper validation of nested address data.
 * <p>
 * HallDTORequest implements {@code Serializable} to facilitate object serialization and deserialization,
 * enabling the transfer of this data object between different layers or processes.
 * <p>
 * Lombok's {@code @Builder} annotation is used to provide an easy way to create instances of HallDTORequest
 * using a fluent API.
 */
@UniqueHall(message = "La salle existe déjà")
@Builder
public record HallDTOCreateRequest(
        @Size(max = 50, message = "La nom de la salle ne doit pas dépasser 50 caractères")
        @NotBlank(message = "Le nom de la salle est obligatoire")
        String name,
        @NotNull
        AddressDTORequest address)
        implements Serializable {
}