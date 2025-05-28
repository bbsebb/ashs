import {Component, effect, inject, input} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatButton, MatFabButton} from '@angular/material/button';
import {CategoryPipe} from '../../../share/pipe/category.pipe';
import {GenderPipe} from '../../../share/pipe/gender.pipe';
import {MatProgressBar} from '@angular/material/progress-bar';
import {DayOfWeekPipe} from '../../../share/pipe/day-of-week.pipe';
import {TimePipe} from '../../../share/pipe/time.pipe';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {MatList, MatListItem} from '@angular/material/list';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Team} from '@app/share/model/team';
import {TeamStore} from '@app/share/store/team.store';
import {TeamsStore} from '@app/share/store/teams.store';

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
    MatFabButton
  ],
  templateUrl: './team.component.html',
  styleUrl: './team.component.css',

})
export class TeamComponent {
  router = inject(Router);
  route = inject(ActivatedRoute);
  uri = input<string>();
  teamStore = inject(TeamStore);
  teamsStore = inject(TeamsStore);


  constructor() {
    effect(() => this.teamStore.uri = this.uri());
  }

  deleteTeam(team: Team) {
    this.teamsStore.deleteTeamWithConfirmation(team)
  }


  goBack() {
    this.router.navigate(['/teams']);
  }

  updateTeam(team: Team) {
    this.router.navigate(['update'], {relativeTo: this.route});
  }
}
