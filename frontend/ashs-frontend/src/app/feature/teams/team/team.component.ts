import {Component, effect, inject, input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {RouterLink} from '@angular/router';
import {CategoryPipe, DayOfWeekPipe, GenderPipe, RoleToFrenchPipe, TeamStore, TimePipe} from 'ngx-training';
import {MatProgressBar} from '@angular/material/progress-bar';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-team',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatDividerModule,
    RouterLink,
    GenderPipe,
    CategoryPipe,
    MatProgressBar,
    RoleToFrenchPipe,
    TimePipe,
    DayOfWeekPipe
  ],
  templateUrl: './team.component.html',
  styleUrl: './team.component.scss'
})
export class TeamComponent {
  private teamStore = inject(TeamStore);
  private logger = inject(NGX_LOGGER);
  uri = input<string>();

  constructor() {
    this.logger.debug('Initialisation du composant Team');

    effect(() => {
      const currentUri = this.uri();
      this.logger.debug('Effet déclenché pour la mise à jour de l\'URI', { uri: currentUri });
      this.teamStore.uri = currentUri;
    });

    this.logger.debug('Signaux initialisés: teamSignal, isLoading, error');
  }

  teamSignal = this.teamStore.team;
  isTeamLoading = this.teamStore.teamResourceIsLoading;
  teamError = this.teamStore.teamResourceError;
  roleCoachesSignal = this.teamStore.roleCoach;
  isRoleCoachesLoading = this.teamStore.roleCoachResourceIsLoading;
  roleCoachesError = this.teamStore.roleCoachResourceError;
  trainingSessionsSignal = this.teamStore.trainingSession;
  isTrainingSessionsLoading = this.teamStore.trainingSessionResourceIsLoading;
  trainingSessionsError = this.teamStore.trainingSessionResourceError;

  reloadTeam() {
    this.logger.info('Rechargement des données de l\'équipe');
    this.teamStore.reloadTeam();
    this.logger.debug('Demande de rechargement de l\'équipe envoyée au store');
  }

  reloadRoleCoaches() {
    this.logger.info('Rechargement des données des coachs de l\'équipe');
    this.teamStore.reloadRoleCoach();
    this.logger.debug('Demande de rechargement des coachs envoyée au store');
  }

  reloadTrainingSessions() {
    this.logger.info('Rechargement des données des séances d\'entraînement');
    this.teamStore.reloadTrainingSession();
    this.logger.debug('Demande de rechargement des séances envoyée au store');
  }
}
