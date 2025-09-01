import {HttpInterceptorFn} from '@angular/common/http';
import {KeycloakService} from '@app/share/service/keycloak.service';
import {inject} from '@angular/core';
import {NGX_LOGGER} from 'ngx-logger';
import {catchError, from, switchMap} from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloakService = inject(KeycloakService);
  const logger = inject(NGX_LOGGER);

  logger.debug('Intercepteur JWT: traitement de la requête', {
    url: req.url,
    method: req.method
  });
  logger.debug('Authentification requise ?', keycloakService.isAuthenticated());
  // Vérifier si la requête nécessite une authentification
  if (req.url.includes('/api') && keycloakService.isAuthenticated()) {
    logger.debug('Ajout du token d\'authentification à la requête');

    return from(keycloakService.getValidToken()).pipe(
      switchMap(token => {
        if (token) {
          const cloned = req.clone({
            setHeaders: {
              Authorization: `Bearer ${token}`
            }
          });
          logger.debug('Token ajouté avec succès');
          return next(cloned);
        } else {
          logger.warn('Token non disponible');
          return next(req);
        }
      }),
      catchError(error => {
        logger.error('Erreur lors de l\'ajout du token:', error);
        return next(req);
      })
    );
  }

  logger.debug('Requête envoyée sans authentification');
  return next(req);
};
