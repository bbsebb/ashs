import {Component, effect, inject, signal, WritableSignal} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {MatFabButton, MatMiniFabButton} from '@angular/material/button';
import {Coach} from '@app/share/model/coach';
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
import {CoachesStore} from '@app/share/store/coaches.store';
import {MatProgressBar} from '@angular/material/progress-bar';
import {LayoutService} from '@app/share/service/layout.service';
import {RouterLink} from '@angular/router';
import {CoachUiService} from '@app/share/service/coach-ui.service';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
  selector: 'app-coaches',
  imports: [
    MatFabButton,
    MatIcon,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatTable,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatHeaderCellDef,
    MatMiniFabButton,
    MatProgressBar,
    MatNoDataRow,
    MatProgressSpinner,
    RouterLink,
  ],
  templateUrl: './coaches.component.html',
  styleUrl: './coaches.component.css'
})
export class CoachesComponent {
  layoutService = inject(LayoutService);
  coachesStore = inject(CoachesStore);
  private readonly coachUiService = inject(CoachUiService);
  dataSource = [] as Coach[];
  displayedColumns: string[] = ['name', 'surname', 'email', 'phone', 'update', 'delete', 'view'];
  isDeleting: WritableSignal<boolean> = signal(false);

  constructor() {
    effect(() => this.dataSource = this.coachesStore.getCoaches())
    effect(() => {
      if (!this.layoutService.isDesktop()) {
        this.displayedColumns = ['name', 'surname', 'update', 'delete', 'view'];
      }
    });
  }

  deleteCoach(coach: Coach) {
    this.isDeleting.set(true);
    this.coachUiService.deleteCoachWithConfirmation(coach).subscribe({
      error: () => this.isDeleting.set(false),
      complete: () => this.isDeleting.set(false)
    })
  }

  protected readonly encodeURIComponent = encodeURIComponent;
}
