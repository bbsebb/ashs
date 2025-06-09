import {Component, effect, inject, input, signal} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {MatList, MatListItem} from '@angular/material/list';
import {RouterLink} from '@angular/router';
import {TeamUiService} from '@app/share/service/team-ui.service';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {CategoryPipe, DayOfWeekPipe, GenderPipe, Team, TeamStore, TimePipe} from 'ngx-training';

@Component({
  selector: 'app-team',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatCardActions,
    MatButton,
    MatCardTitle,
    CategoryPipe,
    GenderPipe,
    MatProgressBar,
    DayOfWeekPipe,
    TimePipe,
    MatIcon,
    MatDivider,
    MatList,
    MatListItem,
    RouterLink,
    MatProgressSpinner
  ],
  templateUrl: './team.component.html',
  styleUrl: './team.component.css',

})
export class TeamComponent {
  uri = input<string>();
  teamStore = inject(TeamStore);
  teamUiService = inject(TeamUiService);
  isDeleting = signal(false);

  constructor() {
    effect(() => this.teamStore.uri = this.uri());
  }

  deleteTeam(team: Team) {
    this.isDeleting.set(true);
    this.teamUiService.deleteTeamWithConfirmation(team).subscribe({
      complete: () => this.isDeleting.set(false),
      error: () => this.isDeleting.set(false)
    })
  }


  protected readonly encodeURIComponent = encodeURIComponent;
}
