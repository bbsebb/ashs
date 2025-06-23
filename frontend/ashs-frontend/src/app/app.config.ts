import {ApplicationConfig, inject, provideAppInitializer, provideZoneChangeDetection} from '@angular/core';
import {provideRouter, withComponentInputBinding} from '@angular/router';
import {routes} from './app.routes';
import {BASE_URL_CONFIG, DELAY} from 'ngx-hal-forms';
import {COACH_SERVICE, CoachService, HALL_SERVICE, HallService, TEAM_SERVICE, TeamService} from 'ngx-training';
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
      useValue: {baseUrl: 'http://localhost:8080/api'}
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
    {provide: DELAY, useValue: 0}]
};
