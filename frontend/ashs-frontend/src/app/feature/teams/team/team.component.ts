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
  uri = input<string>();

  constructor() {
    effect(() => {
      this.teamStore.uri = this.uri();
    });
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
    this.teamStore.reloadTeam();
  }

  reloadRoleCoaches() {
    this.teamStore.reloadRoleCoach();
  }

  reloadTrainingSessions() {
    this.teamStore.reloadTrainingSession();
  }
}
