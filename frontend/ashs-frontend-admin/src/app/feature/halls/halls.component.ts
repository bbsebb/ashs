import {Component, effect, inject, signal, WritableSignal} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {MatFabButton, MatMiniFabButton} from '@angular/material/button';
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
import {MatProgressBar} from '@angular/material/progress-bar';
import {LayoutService} from '@app/share/service/layout.service';
import {RouterLink} from '@angular/router';
import {HallUiService} from '@app/share/service/hall-ui.service';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {Hall, HallsStore} from 'ngx-training';

@Component({
  selector: 'app-halls',
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
  templateUrl: './halls.component.html',
  styleUrl: './halls.component.css'
})
export class HallsComponent {
  layoutService = inject(LayoutService);
  hallsStore = inject(HallsStore);
  private readonly hallUiService = inject(HallUiService);
  dataSource = [] as Hall[];
  displayedColumns: string[] = ['name', 'street', 'postalCode', 'city', 'country', 'update', 'delete', 'view'];
  isDeleting: WritableSignal<boolean> = signal(false);

  constructor() {
    effect(() => this.dataSource = this.hallsStore.halls())
    effect(() => {
      if (!this.layoutService.isDesktop()) {
        this.displayedColumns = ['name', 'city', 'update', 'delete', 'view'];
      }
    });
  }


  deleteHall(hall: Hall) {
    this.isDeleting.set(true);
    this.hallUiService.deleteTeamWithConfirmation(hall).subscribe({
      error: () => this.isDeleting.set(false),
      complete: () => this.isDeleting.set(false)
    })
  }


  protected readonly encodeURIComponent = encodeURIComponent;
}
