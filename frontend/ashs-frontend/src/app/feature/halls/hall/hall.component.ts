import {Component, effect, inject, input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {RouterLink} from '@angular/router';
import {HallStore} from 'ngx-training';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-hall',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatDividerModule,
    RouterLink
  ],
  templateUrl: './hall.component.html',
  styleUrl: './hall.component.scss'
})
export class HallComponent {
  private hallStore = inject(HallStore);
  private logger = inject(NGX_LOGGER);
  uri = input<string>();

  constructor() {
    this.logger.debug('Initialisation du composant Hall');

    effect(() => {
      const currentUri = this.uri();
      this.logger.debug('Effet déclenché pour la mise à jour de l\'URI', { uri: currentUri });
      this.hallStore.uri = currentUri;
    });

    this.logger.debug('Signaux initialisés: hallSignal, isLoading, error');
  }

  hallSignal = this.hallStore.hall;
  isLoading = this.hallStore.hallResourceIsLoading;
  error = this.hallStore.hallResourceError;

  reloadHall() {
    this.logger.info('Rechargement des données de la salle');
    this.hallStore.reloadHall();
    this.logger.debug('Demande de rechargement de la salle envoyée au store');
  }
}
