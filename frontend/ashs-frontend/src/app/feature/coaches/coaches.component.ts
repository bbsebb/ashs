import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { CoachesStore } from 'ngx-training';
import { NGX_LOGGER } from 'ngx-logger';

@Component({
  selector: 'app-coaches',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    RouterLink
  ],
  templateUrl: './coaches.component.html',
  styleUrl: './coaches.component.scss'
})
export class CoachesComponent {
  private coachesStore = inject(CoachesStore);
  private logger = inject(NGX_LOGGER);

  coaches = this.coachesStore.coaches;
  isLoading = this.coachesStore.coachesResourceIsLoading;
  error = this.coachesStore.coachesResourceError;

  constructor() {
    this.logger.debug('Initialisation du composant Coaches');
    this.logger.debug('Signaux initialisés: coaches, isLoading, error');
  }

  reloadCoaches() {
    this.logger.info('Rechargement des données des coachs');
    this.coachesStore.reloadCoaches();
    this.logger.debug('Demande de rechargement des coachs envoyée au store');
  }
}
