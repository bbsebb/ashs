import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';

import {routes} from './app.routes';
import {BASE_URL_CONFIG} from 'ngx-hal-forms';
import {
  COACH_SERVICE,
  CoachStubService,
  HALL_SERVICE,
  HallStubService,
  TEAM_SERVICE,
  TeamStubService
} from 'ngx-training';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({eventCoalescing: true}), provideRouter(routes, withComponentInputBinding()),
    {
      provide: BASE_URL_CONFIG,
      useValue: {baseUrl: 'http://localhost:8082/api'}
    },
    {
      provide: TEAM_SERVICE,
      useClass: TeamStubService
    },
    {
      provide: COACH_SERVICE,
      useClass: CoachStubService
    },
    {
      provide: HALL_SERVICE,
      useClass: HallStubService
    }]
};
