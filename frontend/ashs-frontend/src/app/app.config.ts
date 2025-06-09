import {ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection} from '@angular/core';
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
import {provideHttpClient} from '@angular/common/http';
import {IconRegistryService} from './core/service/service/icon-registry.service';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';


export const appConfig: ApplicationConfig = {

  providers: [
    provideHttpClient(),
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes, withComponentInputBinding()),
    provideAnimationsAsync(),
    provideAppInitializer(() => inject(IconRegistryService).addInstagramIcon()),
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
