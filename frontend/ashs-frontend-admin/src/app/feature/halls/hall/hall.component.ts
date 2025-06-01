import {Component, effect, inject, input, signal, WritableSignal} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {RouterLink} from '@angular/router';
import {Hall} from '@app/share/model/hall';
import {HallStore} from '@app/share/store/hall.store';
import {HallUiService} from '@app/share/service/hall-ui.service';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

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
    MatProgressSpinner,
    RouterLink,
  ],
  templateUrl: './hall.component.html',
  styleUrl: './hall.component.css',
  providers: [HallStore]
})
export class HallComponent {

  uri = input<string>();
  hallStore = inject(HallStore);
  isDeleting: WritableSignal<boolean> = signal(false);
  private readonly hallUiService = inject(HallUiService);

  constructor() {
    effect(() => this.hallStore.uri = this.uri());
  }

  deleteHall(hall: Hall) {
    this.isDeleting.set(true);
    this.hallUiService.deleteTeamWithConfirmation(hall).subscribe({
      error: () => this.isDeleting.set(false),
      complete: () => this.isDeleting.set(false)
    })

  }


}
