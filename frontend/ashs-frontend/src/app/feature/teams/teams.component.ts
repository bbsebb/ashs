import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {RouterLink} from '@angular/router';
import {CategoryPipe, GenderPipe, TeamsStore} from 'ngx-training';

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

  teams = this.teamsStore.teams;
  isLoading = this.teamsStore.teamsResourceIsLoading;
  error = this.teamsStore.teamsResourceError;

  reloadTeams() {

  }
}
