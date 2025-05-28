package fr.hoenheimsports.trainingservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * <p>Implementation of the UserSecurityService interface providing methods for security-related operations.</p>
 * <p>This class is responsible for determining whether the currently authenticated user possesses a specific role.
 * It interacts with the Spring Security context to retrieve authentication details and verifies the user's roles against the required role.</p>
 * <p>The <code>hasRole(String)</code> method:</p>
 * <ul>
 * <li>Checks if the provided role is held by the currently authenticated user.</li>
 * <li>Prepends the "ROLE_" prefix to the input for standard role naming conventions in Spring Security.</li>
 * <li>Validates the role by inspecting the authorities associated with the authenticated principal.</li>
 * <li>Returns false if no authentication is present or if the user lacks the required role.</li>
 * </ul>
 * <p>This service is annotated with <code>@Service</code>, making it a Spring-managed component, and it can
 * be injected wherever user security checks are required.</p>
 */
@Service
public class UserSecurityServiceImpl implements UserSecurityService {

    @Override
    public boolean hasRole(String role) {
        role = "ROLE_".concat(role);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role::equals);
        }
        return false;
    }
}
