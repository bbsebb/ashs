import {inject, Injectable} from '@angular/core';
import {
  ConfirmationDialogComponent
} from '@app/share/component/dialog/confirmation-dialog/confirmation-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {NotificationService} from '@app/share/service/notification.service';
import {Router} from '@angular/router';
import {catchError, EMPTY, switchMap} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Hall, HallsStore} from 'ngx-training';

@Injectable({
  providedIn: 'root'
})
export class HallUiService {

  private readonly matDialog = inject(MatDialog);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly hallsStore = inject(HallsStore);

  deleteTeamWithConfirmation(hall: Hall) {
    const matDialogRef = this.createDeleteConfirmation(hall);
    return matDialogRef.afterClosed()
      .pipe(
        switchMap(res => {
          if (res) {
            return this.hallsStore.deleteHall(hall)
          } else {
            return EMPTY;
          }
        }),
        tap(() => this.notificationService.showSuccess(`La salle a été supprimée`)),
        tap(() => void this.router.navigate(['/halls'])),
        catchError((err) => {
          console.log("Error", err);
          this.notificationService.showError('Une erreur est survenue lors de la suppression');
          return EMPTY;
        })
      );
  }

  createDeleteConfirmation(hall: Hall) {
    return this.matDialog.open<ConfirmationDialogComponent, {
      title: string,
      content: string
    }, boolean>(ConfirmationDialogComponent, {
      data: {
        title: 'Suppression',
        content: `Etes-vous sur de vouloir supprimer : ${hall.name} ?`
      },
    });
  }

}
