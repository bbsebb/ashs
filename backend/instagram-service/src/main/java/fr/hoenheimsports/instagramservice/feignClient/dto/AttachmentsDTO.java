package fr.hoenheimsports.instagramservice.feignClient.dto;

import java.util.List;

/**
 * Conteneur pour la liste des pièces jointes.
 *
 * @param data La liste des objets de pièce jointe.
 */
public record AttachmentsDTO(
        List<AttachmentDTO> data
) {
}
