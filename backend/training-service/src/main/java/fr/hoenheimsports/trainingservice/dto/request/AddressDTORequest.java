package fr.hoenheimsports.trainingservice.dto.request;

import fr.hoenheimsports.trainingservice.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

/**
 * AddressDTORequest is a Data Transfer Object (DTO) used to encapsulate the input data representing an address. ({@link Address}).
 * It is intended for use in request payloads when creating or updating address-related entities.
 *
 * This DTO includes the following fields:
 * - street: Represents the street name. It must not be blank and can have a maximum length of 100 characters.
 * - city: Represents the city name. It must not be blank and can have a maximum length of 50 characters.
 * - postalCode: Represents the postal code. It must match the pattern of exactly 5 numeric digits.
 * - country: Represents the country name. It must not be blank and can have a maximum length of 50 characters.
 *
 * The constraints on these fields ensure data validity and consistency. Validation annotations are used to enforce
 * required formats, maximum lengths, and mandatory presence.
 *
 * This class implements the Serializable interface, allowing it to be serialized and deserialized when needed,
 * such as during data transfer. It uses the Lombok @Builder annotation to facilitate straightforward object
 * construction.
 */
@Builder
public record AddressDTORequest(
        @NotBlank(message = "La rue est obligatoire")
        @Size(max = 100, message = "La rue ne doit pas dépasser 100 caractères")
        String street,

        @NotBlank(message = "La ville est obligatoire")
        @Size(max = 50, message = "La ville ne doit pas dépasser 50 caractères")
        String city,

        @Pattern(message = "Le code postal doit être composé de 5 chiffres", regexp = "\\d{5}")
        String postalCode,

        @NotBlank(message = "Le pays est obligatoire")
        @Size(max = 50, message = "Le pays ne doit pas dépasser 50 caractères")
        String country
) implements Serializable {
}
