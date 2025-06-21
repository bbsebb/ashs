import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { HallsStore } from 'ngx-training';

@Component({
  selector: 'app-halls',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    RouterLink
  ],
  templateUrl: './halls.component.html',
  styleUrl: './halls.component.scss'
})
export class HallsComponent {
  private hallsStore = inject(HallsStore);

  halls = this.hallsStore.halls;
  isLoading = this.hallsStore.hallsResourceIsLoading;
  error = this.hallsStore.hallsResourceError;

  reloadHalls() {
    this.hallsStore.reloadHalls();
  }
}
