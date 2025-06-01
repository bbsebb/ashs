import {inject, Injectable} from '@angular/core';
import {Team} from '@app/share/model/team';
import {GenderPipe} from '@app/share/pipe/gender.pipe';
import {CategoryPipe} from '@app/share/pipe/category.pipe';
import {
  ConfirmationDialogComponent
} from '@app/share/component/dialog/confirmation-dialog/confirmation-dialog.component';
import {NotificationService} from '@app/share/service/notification.service';
import {Router} from '@angular/router';
import {TeamsStore} from '@app/share/store/teams.store';
import {MatDialog} from '@angular/material/dialog';
import {catchError, EMPTY, switchMap} from 'rxjs';
import {tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TeamUiService {

  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly teamsStore = inject(TeamsStore);
  private readonly matDialog = inject(MatDialog);

  /**
   * Deletes a team with confirmation dialog
   * @param team The team to delete
   */
  deleteTeamWithConfirmation(team: Team) {
    const matDialogRef = this.createDeleteConfirmation(team);
    return matDialogRef.afterClosed().pipe(
      switchMap(res => {
        if (res) {
          return this.teamsStore.deleteTeam(team);
        } else {
          return EMPTY;
        }
      }),
      tap(() => this.notificationService.showSuccess(`L'équipe a été supprimée`)),
      tap(() => void this.router.navigate(['/teams'])),
      catchError(() => {
        this.notificationService.showError('Une erreur est survenue lors de la suppression');
        return EMPTY;
      })
    )
  };

  createDeleteConfirmation(team: Team) {
    const genderDisplay = new GenderPipe().transform(team.gender);
    const categoryDisplay = new CategoryPipe().transform(team.category);

    return this.matDialog.open<ConfirmationDialogComponent, {
      title: string,
      content: string
    }, boolean>(ConfirmationDialogComponent, {
      data: {
        title: 'Suppression',
        content: `Etes-vous sur de vouloir supprimer : ${genderDisplay} ${categoryDisplay} ${team.teamNumber}  ?`
      },
    });
  }
}
