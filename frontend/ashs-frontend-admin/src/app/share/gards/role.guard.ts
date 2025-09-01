import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {KeycloakService} from '@app/share/service/keycloak.service';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const keycloakService = inject(KeycloakService);
    const router = inject(Router);

    if (!keycloakService.isAuthenticated()) {
      console.log('[RoleGuard] Utilisateur non authentifié');
      void keycloakService.login();
      return false;
    }

    const hasRequiredRole = keycloakService.hasAnyRole(allowedRoles);

    if (!hasRequiredRole) {
      console.log('[RoleGuard] Accès refusé - rôles requis:', allowedRoles);
      console.log('[RoleGuard] Rôles utilisateur:', keycloakService.getUserRoles());
      void router.navigate(['/unauthorized']);
      return false;
    }

    return true;
  };

};
