import {CanActivateFn} from '@angular/router';
import {inject} from '@angular/core';
import {KeycloakService} from '@app/share/service/keycloak.service';

export const authGard: CanActivateFn = (route, state) => {
  const keycloakService = inject(KeycloakService);

  if (keycloakService.isAuthenticated()) {
    return true;
  }

  console.log('[AuthGuard] Utilisateur non authentifié, redirection vers la connexion');
  void keycloakService.login();
  return false;

};
