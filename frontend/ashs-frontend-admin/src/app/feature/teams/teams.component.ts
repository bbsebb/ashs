import {Component, effect, inject, signal, WritableSignal} from '@angular/core';
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
import {CategoryPipe, GenderPipe, Team, TeamsStore} from 'ngx-training';
import {RouterLink} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {LayoutService} from '@app/share/service/layout.service';
import {TeamUiService} from '@app/share/service/team-ui.service';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

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
    MatFabButton,
    MatProgressSpinner,
    RouterLink
  ],
  templateUrl: './teams.component.html',
  styleUrl: './teams.component.css',

})
export class TeamsComponent {
  layoutService = inject(LayoutService);
  teamsStore = inject(TeamsStore);
  teamUiService = inject(TeamUiService);
  dataSource = [] as Team[];
  displayedColumns: string[] = ['team', 'update', 'delete', 'view'];
  isDeleting: WritableSignal<boolean> = signal(false)

  constructor() {
    effect(() => this.dataSource = this.teamsStore.teams())
  }


  deleteTeam(team: Team) {
    this.isDeleting.set(true);
    this.teamUiService.deleteTeamWithConfirmation(team).subscribe({
      complete: () => this.isDeleting.set(false),
      error: () => this.isDeleting.set(false)
    })
  }


  handlePageEvent($event: PageEvent) {
    this.teamsStore.paginationOption = {size: $event.pageSize, page: $event.pageIndex};
  }


  protected readonly encodeURIComponent = encodeURIComponent;
}
