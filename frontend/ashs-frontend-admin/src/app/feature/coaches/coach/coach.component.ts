import {Component, effect, inject, input, signal, WritableSignal} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {RouterLink} from '@angular/router';
import {Coach} from '@app/share/model/coach';
import {CoachStore} from '@app/share/store/coach.store';
import {CoachUiService} from '@app/share/service/coach-ui.service';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-coach',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatCardActions,
    MatButton,
    MatCardTitle,
    MatProgressBar,
    MatIcon,
    MatDivider,
    MatProgressSpinner,
    RouterLink,
  ],
  templateUrl: './coach.component.html',
  styleUrl: './coach.component.css',
  providers: [CoachStore]
})
export class CoachComponent {

  uri = input<string>();
  coachStore = inject(CoachStore);
  isDeleting: WritableSignal<boolean> = signal(false);
  private readonly coachUiService = inject(CoachUiService);

  constructor() {
    effect(() => this.coachStore.uri = this.uri());
  }

  deleteCoach(coach: Coach) {
    this.isDeleting.set(true);
    this.coachUiService.deleteCoachWithConfirmation(coach).subscribe({
      error: () => this.isDeleting.set(false),
      complete: () => this.isDeleting.set(false)
    })
  }
}
