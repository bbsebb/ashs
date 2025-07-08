import {HttpInterceptorFn} from '@angular/common/http';
import {KeycloakService} from '@app/share/service/keycloak.service';
import {inject} from '@angular/core';
import {NGX_LOGGER, NgxLoggerService} from 'ngx-logger';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloakService = inject(KeycloakService);
  const logger = inject(NGX_LOGGER);

  logger.debug('Intercepteur JWT: traitement de la requête', {
    url: req.url,
    method: req.method
  });

  const token = keycloakService.getToken();

  if (token) {
    logger.debug('Token Keycloak trouvé, ajout de l\'en-tête Authorization');

    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    logger.debug('Requête clonée avec l\'en-tête Authorization');
    return next(cloned);
  }

  logger.warn('Aucun token Keycloak trouvé, la requête sera envoyée sans authentification', {
    url: req.url
  });
  return next(req);
};
