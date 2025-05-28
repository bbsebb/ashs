import {Component, effect, inject, untracked} from '@angular/core';
import {Gender} from '@app/share/model/gender';
import {Category} from '@app/share/model/category';
import {Team} from '@app/share/model/team';
import {TrainingSession} from '@app/share/model/training-session';
import {RoleCoach} from '@app/share/model/role-coach';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatNoDataRow,
  MatRow,
  MatRowDef,
  MatTable
} from '@angular/material/table';
import {MatButton} from '@angular/material/button';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatProgressBar} from '@angular/material/progress-bar';
import {TeamsStore} from '@app/share/store/teams.store';
import {CreateTeamDTORequest} from '@app/share/service/dto/create-team-d-t-o-request';

@Component({
  selector: 'app-test',
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    MatButton,
    MatPaginator,
    MatNoDataRow,
    MatProgressBar,
  ],
  templateUrl: './test.component.html',
  styleUrl: './test.component.css'
})
export class TestComponent {

  displayedColumns: string[] = ['id', 'gender', 'category', 'teamNumber', 'delete'];
  teamsStore = inject(TeamsStore);

  dataSource = [] as Team[];

  constructor() {
    //effect(() => console.log(this.teamStore.team(), this.teamStore.trainingSessions(), this.teamStore.roleCoaches()));
    effect(() => {
      this.dataSource = this.teamsStore.getTeams();
    })
  }


  addTeam() {

    const t: CreateTeamDTORequest = {
      gender: Gender.Masculine,
      category: Category.U11,
      teamNumber: Math.floor(Math.random() * 1000) + 1,
    };
    console.log(t)
    this.teamsStore.createTeam(t, [], []).subscribe(console.log);
  }

  delete() {


  }


  protected readonly untracked = untracked;

  select(team: Team) {

  }

  addTrainingSession() {
    /*    this.teamService.addTrainingSession({
          "hallId": 1,
          "trainingSessionDTORequest": {
            "timeSlot": {
              "dayOfWeek": DayOfWeek.MONDAY,
              "startTime": "10:00:00",
              "endTime": "11:00:00"
            }
          }
        });*/
  }

  loadTrainingSession() {

  }

  removeTrainingSession(t: TrainingSession) {

  }

  loadCoaches() {

  }

  addCoach() {
    // this.teamStore.addRoleCoach({coachId: 1, teamId: 1, role: Role.MAIN});
  }


  deleteRoleCoach(roleCoach: RoleCoach) {

  }

  handlePageEvent($event: PageEvent) {
    this.teamsStore.paginationOption = {size: $event.pageSize, page: $event.pageIndex};
  }

  deleteTeam(team: Team) {
    this.teamsStore.deleteTeam(team).subscribe();
  }

  goTo(team: Team) {
    console.log(team);
  }

  paginated() {
    this.teamsStore.paginationOption = {size: 1, page: 0};
  }

  all() {
    this.teamsStore.paginationOption = 'all';
  }
}


