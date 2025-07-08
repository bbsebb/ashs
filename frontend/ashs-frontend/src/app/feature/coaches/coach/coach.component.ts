import {Component, effect, inject, input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {RouterLink} from '@angular/router';
import {CoachStore} from 'ngx-training';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-coach',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatDividerModule,
    RouterLink
  ],
  templateUrl: './coach.component.html',
  styleUrl: './coach.component.scss'
})
export class CoachComponent {
  private coachStore = inject(CoachStore);
  private logger = inject(NGX_LOGGER);
  uri = input<string>();

  constructor() {
    this.logger.debug('Initialisation du composant Coach');

    effect(() => {
      const currentUri = this.uri();
      this.logger.debug('Effet déclenché pour la mise à jour de l\'URI', { uri: currentUri });
      this.coachStore.uri = currentUri;
    });

    this.logger.debug('Signaux initialisés: coachSignal, isLoading, error');
  }

  coachSignal = this.coachStore.coach;
  isLoading = this.coachStore.coachResourceIsLoading;
  error = this.coachStore.coachResourceError;

  reloadCoach() {
    this.logger.info('Rechargement des données du coach');
    this.coachStore.reloadCoach();
    this.logger.debug('Demande de rechargement du coach envoyée au store');
  }
}
