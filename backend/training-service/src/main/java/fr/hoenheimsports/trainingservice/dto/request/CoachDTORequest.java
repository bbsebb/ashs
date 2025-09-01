package fr.hoenheimsports.trainingservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link fr.hoenheimsports.trainingservice.model.Coach}
 */
@Builder
public record CoachDTORequest(@NotBlank(message = "Le nom du coach ne doit pas être vide") String name,
                              @NotBlank(message = "le prénom du coach ne doit pas être vide") String surname,
                              @Email(message = "l'email du coach n'est pas valide") String email,
                              @Pattern(
                                      message = "Le numéro de téléphone doit contenir entre 10 et 15 chiffres consécutifs, avec éventuellement un signe '+' au début. Les espaces, tirets ou autres caractères spéciaux ne sont pas autorisés.",
                                      regexp = "^$|\\+?[0-9]{10,15}"
                              )
                              String phone) implements Serializable {
}