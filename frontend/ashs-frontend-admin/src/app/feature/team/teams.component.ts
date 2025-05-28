import {Component, effect, inject} from '@angular/core';
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
} from "@angular/material/table";
import {MatProgressBar} from "@angular/material/progress-bar";
import {MatFabButton, MatMiniFabButton} from '@angular/material/button';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {Team} from '@app/share/model/team';
import {CategoryPipe} from '@app/share/pipe/category.pipe';
import {GenderPipe} from '@app/share/pipe/gender.pipe';
import {ActivatedRoute, Router} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {TeamsStore} from '@app/share/store/teams.store';

@Component({
  selector: 'app-team',
  imports: [
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatPaginator,
    MatProgressBar,
    MatRow,
    MatRowDef,
    MatTable,
    MatHeaderCellDef,
    MatNoDataRow,
    CategoryPipe,
    GenderPipe,
    MatIcon,
    MatMiniFabButton,
    MatFabButton
  ],
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.css',

})
export class TeamsComponent {

  teamsStore = inject(TeamsStore);
  router = inject(Router);
  route = inject(ActivatedRoute);
  dataSource = [] as Team[];
  displayedColumns: string[] = ['team', 'update', 'delete', 'view'];


  constructor() {
    effect(() => this.dataSource = this.teamsStore.getTeams())
  }

  addTeam() {
    this.router.navigate(['create'], {relativeTo: this.route});
  }

  deleteTeam(team: Team) {
    this.teamsStore.deleteTeamWithConfirmation(team)
  }


  updateTeam(team: Team) {
    this.router.navigate([encodeURIComponent(team._links.self.href), 'update'], {relativeTo: this.route});
  }

  viewTeam(team: Team) {
    this.router.navigate([encodeURIComponent(team._links.self.href)], {relativeTo: this.route});
  }

  handlePageEvent($event: PageEvent) {
    this.teamsStore.paginationOption = {size: $event.pageSize, page: $event.pageIndex};
  }


}
