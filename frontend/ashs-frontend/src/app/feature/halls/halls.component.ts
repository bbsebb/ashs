import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { HallsStore } from 'ngx-training';
import { NGX_LOGGER } from 'ngx-logger';

@Component({
  selector: 'app-halls',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    RouterLink
  ],
  templateUrl: './halls.component.html',
  styleUrl: './halls.component.scss'
})
export class HallsComponent {
  private hallsStore = inject(HallsStore);
  private logger = inject(NGX_LOGGER);

  halls = this.hallsStore.halls;
  isLoading = this.hallsStore.hallsResourceIsLoading;
  error = this.hallsStore.hallsResourceError;

  constructor() {
    this.logger.debug('Initialisation du composant Halls');
    this.logger.debug('Signaux initialisés: halls, isLoading, error');
  }

  reloadHalls() {
    this.logger.info('Rechargement des données des salles');
    this.hallsStore.reloadHalls();
    this.logger.debug('Demande de rechargement des salles envoyée au store');
  }
}
