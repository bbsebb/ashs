package fr.hoenheimsports.trainingservice.service;

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
