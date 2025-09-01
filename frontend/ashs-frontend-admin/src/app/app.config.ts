import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';

import {routes} from './app.routes';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {BASE_URL_CONFIG, DELAY} from 'ngx-hal-forms';
import {COACH_SERVICE, CoachService, HALL_SERVICE, HallService, TEAM_SERVICE, TeamService} from 'ngx-training';
import {jwtInterceptor} from '@app/core/interceptor/jwt.interceptor';
import {NGX_LOGGER, NgxConsoleLoggerService} from 'ngx-logger';
import {environment} from '@environments/environment';
import {provideKeycloak} from 'keycloak-angular';


export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloak({
      config: {
        url: `${environment.keycloak.url}`,
        realm: `${environment.keycloak.realm}`,
        clientId: `${environment.keycloak.clientId}`
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        redirectUri: window.location.origin + '/home'
      }
    }),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes, withComponentInputBinding()),
    provideAnimations(),
    {
      provide: BASE_URL_CONFIG,
      useValue: {baseUrl: environment.baseUrl}
    },
    {
      provide: DELAY,
      useValue: 150
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
    },
    {
      provide: NGX_LOGGER,
      useClass: NgxConsoleLoggerService
    },
  ]
};
