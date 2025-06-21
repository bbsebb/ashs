import {ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';

import {routes} from './app.routes';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {BASE_URL_CONFIG} from 'ngx-hal-forms';
import {COACH_SERVICE, CoachService, HALL_SERVICE, HallService, TEAM_SERVICE, TeamService} from 'ngx-training';
import {KeycloakService} from './share/service/keycloak.service';
import {jwtInterceptor} from '@app/core/interceptor/jwt.interceptor';


export const appConfig: ApplicationConfig = {
  providers: [
    provideAppInitializer(() => {
      return inject(KeycloakService).init();
    }),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes, withComponentInputBinding()),
    provideAnimations(),
    {
      provide: BASE_URL_CONFIG,
      useValue: {baseUrl: 'http://localhost:8082/api'}
    },
    {
      provide: TEAM_SERVICE,
      useClass: TeamService
    },
    {
      provide: COACH_SERVICE,
      useClass: CoachService
    },
    {
      provide: HALL_SERVICE,
      useClass: HallService
    }
  ]
};
