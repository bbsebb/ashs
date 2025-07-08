package fr.hoenheimsports.trainingservice.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an address entity with details such as street, city, postal code, and country.
 * This class is marked as {@link Embeddable}, meaning it can be embedded
 * within other entities as part of their persistence mapping.
 * <p>
 * Validations are provided for each attribute to ensure compliance with specific business rules:
 * - `street`: Mandatory, with a maximum length of 100 characters.
 * - `city`: Mandatory, with a maximum length of 50 characters.
 * - `postalCode`: Must follow a defined pattern of exactly 5 digits.
 * - `country`: Mandatory, with a maximum length of 50 characters.
 * <p>
 * This class uses Lombok annotations for boilerplate code reduction:
 * - {@link Data} for generated getters, setters, equals, hashCode, and toString methods.
 * - {@link NoArgsConstructor} for creating a no-argument constructor.
 * - {@link AllArgsConstructor} for creating an all-argument constructor.
 * - {@link Builder} for enabling the builder pattern for object creation.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @NotBlank(message = "La rue est obligatoire")
    @Size(max = 100, message = "La rue ne doit pas dépasser 100 caractères")
    private String street;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 50, message = "La ville ne doit pas dépasser 50 caractères")
    private String city;

    @Pattern(regexp = "\\d{5}", message = "Le code postal doit être composé de 5 chiffres")
    private String postalCode;

    @NotBlank(message = "Le pays est obligatoire")
    @Size(max = 50, message = "Le pays ne doit pas dépasser 50 caractères")
    private String country;

}