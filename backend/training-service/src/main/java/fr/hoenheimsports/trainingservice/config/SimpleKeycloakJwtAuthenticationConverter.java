package fr.hoenheimsports.trainingservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleKeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        // Extraire les rôles depuis "realm_access.roles"
        Collection<? extends GrantedAuthority> authorities = extractRealmRoles(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        // Extrait la liste de rôles depuis le claim "realm_access.roles"
        var realmRoles = jwt.getClaimAsMap("realm_access").get("roles");
        if (realmRoles instanceof List<?> realmRolesList && !realmRolesList.isEmpty()) {
            // Ajoute le préfixe "ROLE_" requis par Spring Security
            return realmRolesList.stream()
                    .map(role -> (String) role)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet());

        }
        return List.of(); // Retourne une liste vide si aucun rôle n'est trouvé
    }
}
