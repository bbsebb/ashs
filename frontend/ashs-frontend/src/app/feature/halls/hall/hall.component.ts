import {Component, effect, inject, input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {RouterLink} from '@angular/router';
import {HallStore} from 'ngx-training';

@Component({
  selector: 'app-hall',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatDividerModule,
    RouterLink
  ],
  templateUrl: './hall.component.html',
  styleUrl: './hall.component.scss'
})
export class HallComponent {
  private hallStore = inject(HallStore);
  uri = input<string>();

  constructor() {
    effect(() => {
      this.hallStore.uri = this.uri();
    });
  }

  hallSignal = this.hallStore.hall;
  isLoading = this.hallStore.hallResourceIsLoading;
  error = this.hallStore.hallResourceError;

  reloadHall() {
    this.hallStore.reloadHall();
  }
}
