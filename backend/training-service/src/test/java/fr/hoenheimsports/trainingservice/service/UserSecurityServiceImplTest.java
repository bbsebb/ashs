package fr.hoenheimsports.trainingservice.service;

import fr.hoenheimsports.trainingservice.TestcontainersConfiguration;
import fr.hoenheimsports.trainingservice.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@SpringBootTest
@Import({TestcontainersConfiguration.class, TestSecurityConfig.class})
@ActiveProfiles("test")
@ContextConfiguration
public class UserSecurityServiceImplTest {

    @InjectMocks
    private UserSecurityServiceImpl userSecurityService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldReturnTrueWhenUserHasRole() {
        // Simulation d'un utilisateur avec un rôle
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        when(authentication.isAuthenticated()).thenReturn(true);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(authority);
        when(authentication.getAuthorities()).thenAnswer(_ -> authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        assertThat(userSecurityService.hasRole("ADMIN")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserHasNoRole() {
        // Simulation d'un utilisateur sans le rôle attendu
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        assertThat(userSecurityService.hasRole("ADMIN")).isFalse();

    }

    @Test
    void shouldReturnFalseWhenAuthenticationIsNull() {
        // Cas où aucun utilisateur n'est authentifié
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThat(userSecurityService.hasRole("ADMIN")).isFalse();
    }
}
