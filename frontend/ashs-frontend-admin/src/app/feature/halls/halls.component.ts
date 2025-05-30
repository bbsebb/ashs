import {Component, effect, inject} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {MatFabButton, MatMiniFabButton} from '@angular/material/button';
import {Hall} from '@app/share/model/hall';
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
import {HallsStore} from '@app/share/store/halls.store';
import {MatProgressBar} from '@angular/material/progress-bar';
import {LayoutService} from '@app/share/service/layout.service';
import {ActivatedRoute, Router} from '@angular/router';

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
  ],
  templateUrl: './halls.component.html',
  styleUrl: './halls.component.css'
})
export class HallsComponent {
  layoutService = inject(LayoutService);
  hallsStore = inject(HallsStore);
  router = inject(Router);
  route = inject(ActivatedRoute);
  dataSource = [] as Hall[];
  displayedColumns: string[] = ['name', 'street', 'postalCode', 'city', 'country', 'update', 'delete', 'view'];

  constructor() {
    effect(() => this.dataSource = this.hallsStore.getHalls())
    effect(() => {
      if (!this.layoutService.isDesktop()) {
        this.displayedColumns = ['name', 'city', 'update', 'delete', 'view'];
      }
    });
  }

  addHall() {
    void this.router.navigate(['create'], {relativeTo: this.route});

  }

  viewHall(hall: Hall) {
    void this.router.navigate([encodeURIComponent(hall._links.self.href)], {relativeTo: this.route});

  }

  deleteHall(hall: Hall) {
    this.hallsStore.deleteTeamWithConfirmation(hall)
  }

  updateHall(hall: Hall) {
    void this.router.navigate([encodeURIComponent(hall._links.self.href), 'update'], {relativeTo: this.route});

  }
}
