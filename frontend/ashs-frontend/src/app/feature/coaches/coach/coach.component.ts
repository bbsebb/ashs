import {Component, effect, inject, input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {RouterLink} from '@angular/router';
import {CoachStore} from 'ngx-training';

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
  uri = input<string>();

  constructor() {
    effect(() => {
      this.coachStore.uri = this.uri();
    });
  }

  coachSignal = this.coachStore.coach;
  isLoading = this.coachStore.coachResourceIsLoading;
  error = this.coachStore.coachResourceError;


  reloadCoach() {
    this.coachStore.reloadCoach();
  }
}
