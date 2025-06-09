import {inject, Injectable} from '@angular/core';
import {
  ConfirmationDialogComponent
} from '@app/share/component/dialog/confirmation-dialog/confirmation-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {NotificationService} from '@app/share/service/notification.service';
import {Router} from '@angular/router';

import {catchError, EMPTY, switchMap} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Coach, CoachesStore} from 'ngx-training';

@Injectable({
  providedIn: 'root'
})
export class CoachUiService {

  private readonly matDialog = inject(MatDialog);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly coachesStore = inject(CoachesStore);

  deleteCoachWithConfirmation(coach: Coach) {
    const matDialogRef = this.createDeleteConfirmation(coach);
    return matDialogRef.afterClosed()
      .pipe(
        switchMap(res => {
          if (res) {
            return this.coachesStore.deleteCoach(coach)
          } else {
            return EMPTY;
          }
        }),
        tap(() => this.notificationService.showSuccess(`Le coach a été supprimé`)),
        tap(() => void this.router.navigate(['/coaches'])),
        catchError(() => {
          this.notificationService.showError('Une erreur est survenue lors de la suppression');
          return EMPTY;
        })
      );
  }

  createDeleteConfirmation(coach: Coach) {
    return this.matDialog.open<ConfirmationDialogComponent, {
      title: string,
      content: string
    }, boolean>(ConfirmationDialogComponent, {
      data: {
        title: 'Suppression',
        content: `Etes-vous sur de vouloir supprimer : ${coach.name} ${coach.surname} ?`
      },
    });
  }
}
