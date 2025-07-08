package fr.hoenheimsports.facebookservice.feignClient.dto;

import java.util.List;

/**
 * Le DTO racine qui correspond à l'objet JSON principal.
 *
 * @param data La liste des publications (posts).
 */
public record GraphApiResponse(
        List<FeedDTO> data
) {
}
