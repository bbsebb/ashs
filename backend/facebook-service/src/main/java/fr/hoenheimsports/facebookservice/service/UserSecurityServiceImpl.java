package fr.hoenheimsports.facebookservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Implementation of the UserSecurityService interface for user security operations.
 * 
 * <p>This service is responsible for determining whether the currently authenticated user
 * possesses a specific role. It interacts with the Spring Security context to retrieve
 * authentication details and verifies the user's roles against the required role.</p>
 * 
 * <p>The service automatically prepends the "ROLE_" prefix to role names to adhere to
 * Spring Security's standard role naming convention.</p>
 * 
 * @since 1.0
 */
@Service
@Slf4j
public class UserSecurityServiceImpl implements UserSecurityService {

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation checks if the currently authenticated user has the specified role.
     * It prepends "ROLE_" to the role name to match Spring Security's convention, then
     * retrieves the current authentication from the security context and checks if any
     * of the user's authorities match the role.</p>
     * 
     * @param role The role to check (without the "ROLE_" prefix)
     * @return true if the user has the specified role, false otherwise
     */
    @Override
    public boolean hasRole(String role) {
        log.debug("Vérification si l'utilisateur possède le rôle: {}", role);
        role = "ROLE_".concat(role);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            log.debug("Utilisateur authentifié: {}", authentication.getName());
            boolean hasRole = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role::equals);
            log.debug("L'utilisateur {} le rôle {}", hasRole ? "possède" : "ne possède pas", role);
            return hasRole;
        }
        log.debug("Aucun utilisateur authentifié ou authentification invalide");
        return false;
    }
}
