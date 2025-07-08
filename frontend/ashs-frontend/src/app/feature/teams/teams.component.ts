import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {RouterLink} from '@angular/router';
import {CategoryPipe, GenderPipe, TeamsStore} from 'ngx-training';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-teams',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    RouterLink,
    CategoryPipe,
    GenderPipe
  ],
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.scss'
})
export class TeamsComponent {
  private teamsStore = inject(TeamsStore);
  private logger = inject(NGX_LOGGER);

  teams = this.teamsStore.teams;
  isLoading = this.teamsStore.teamsResourceIsLoading;
  error = this.teamsStore.teamsResourceError;

  constructor() {
    this.logger.debug('Initialisation du composant Teams');
    this.logger.debug('Signaux initialisés: teams, isLoading, error');
  }

  reloadTeams() {
    this.logger.info('Rechargement des données des équipes');
    this.teamsStore.reloadTeams();
    this.logger.debug('Demande de rechargement des équipes envoyée au store');
  }
}
