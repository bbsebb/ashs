import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { CoachesStore } from 'ngx-training';

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

  coaches = this.coachesStore.coaches;
  isLoading = this.coachesStore.coachesResourceIsLoading;
  error = this.coachesStore.coachesResourceError;

  reloadCoaches() {
    this.coachesStore.reloadCoaches();
  }
}
