package fr.hoenheimsports.facebookservice.service;

/**
 * Service interface for user security operations.
 * 
 * <p>This interface defines the contract for checking user roles and permissions
 * within the application. It provides methods to verify if the currently authenticated
 * user has specific roles or permissions.</p>
 * 
 * @since 1.0
 */
public interface UserSecurityService {
    /**
     * Checks if the currently authenticated user has the specified role.
     * The input role is automatically prefixed with "ROLE_" to adhere
     * to Spring Security's standard role naming convention.
     *
     * @param role the name of the role to check (without the "ROLE_" prefix)
     * @return true if the user has the specified role, false otherwise
     */
    boolean hasRole(String role);
}
