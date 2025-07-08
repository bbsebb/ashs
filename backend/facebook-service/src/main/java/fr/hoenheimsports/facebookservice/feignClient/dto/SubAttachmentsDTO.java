package fr.hoenheimsports.facebookservice.feignClient.dto;

import java.util.List;

/**
 * Conteneur pour la liste des sous-pièces jointes (dans un album).
 *
 * @param data La liste des objets de sous-pièce jointe.
 */
public record SubAttachmentsDTO(
        List<SubAttachmentDTO> data
) {
}
