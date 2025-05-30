import {Component, effect, inject, input} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {MatList, MatListItem} from '@angular/material/list';
import {Router} from '@angular/router';
import {Hall} from '@app/share/model/hall';
import {HallStore} from '@app/share/store/hall.store';

@Component({
  selector: 'app-hall',
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
    MatList,
    MatListItem
  ],
  templateUrl: './hall.component.html',
  styleUrl: './hall.component.css',
  providers: [HallStore]
})
export class HallComponent {
  router = inject(Router);
  uri = input<string>();
  hallStore = inject(HallStore);

  constructor() {
    effect(() => this.hallStore.uri = this.uri());
  }

  deleteHall(hall: Hall) {
    // Implement delete functionality
    console.log('Delete hall', hall);
  }

  goBack() {
    void this.router.navigate(['/halls']);
  }

  updateHall(hall: Hall) {
    // Implement update functionality
    console.log('Update hall', hall);
  }
}
