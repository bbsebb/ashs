package fr.hoenheimsports.facebookservice.mapper;

import fr.hoenheimsports.facebookservice.controller.dto.AccessTokenDTOResponse;
import fr.hoenheimsports.facebookservice.feignClient.dto.AccessTokenDTO;
import fr.hoenheimsports.facebookservice.model.AccessToken;
import org.mapstruct.*;

import java.time.Instant;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccessTokenMapper {
    @Mapping(target = "expireIn", source = "expireIn", qualifiedByName = "expireInToInstant")
    AccessToken toEntity(AccessTokenDTO accessTokenDTO);

    AccessTokenDTOResponse toDto(AccessToken accessToken);

    @Named("expireInToInstant")
    default Instant expireInToInstant(int expireIn) {
        return Instant.now().plusSeconds(expireIn);
    }

}