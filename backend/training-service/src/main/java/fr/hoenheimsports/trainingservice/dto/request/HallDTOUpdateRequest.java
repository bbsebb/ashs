package fr.hoenheimsports.trainingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;


@Builder
public record HallDTOUpdateRequest(
        @Size(max = 50, message = "La nom de la salle ne doit pas dépasser 50 caractères")
        @NotBlank(message = "Le nom de la salle est obligatoire")
        String name,
        @NotNull
        AddressDTORequest address)
        implements Serializable {
}
