import {HttpInterceptorFn} from '@angular/common/http';
import {KeycloakService} from '@app/share/service/keycloak.service';
import {inject} from '@angular/core';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloakService = inject(KeycloakService);

  const token = keycloakService.getToken();

  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }
  return next(req);
};
