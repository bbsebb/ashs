import {inject, Injectable} from '@angular/core';
import {Team} from '@app/share/model/team';
import {
  ConfirmationDialogComponent
} from '@app/share/component/dialog/confirmation-dialog/confirmation-dialog.component';
import {GenderPipe} from '@app/share/pipe/gender.pipe';
import {CategoryPipe} from '@app/share/pipe/category.pipe';
import {MatDialog} from '@angular/material/dialog';
import {
  AddTrainingSessionInTeamDTORequest,
  toAddTrainingSessionInTeamDTORequest
} from '@app/share/service/dto/add-training-session-in-team-d-t-o-request';
import {catchError, forkJoin, Observable, of, switchMap, throwError} from 'rxjs';
import {TrainingSession} from '@app/share/model/training-session';
import {
  AddRoleCoachInTeamDTORequest,
  toAddRoleCoachInTeamDTORequest
} from '@app/share/service/dto/add-role-coach-in-team-d-t-o-request';
import {RoleCoach} from '@app/share/model/role-coach';
import {HalFormService} from '@app/share/service/hal-form.service';
import {CreateTeamDTORequest} from '@app/share/service/dto/create-team-d-t-o-request';
import {FormTrainingSessionDTO} from '@app/share/service/dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '@app/share/service/dto/form-role-coach-d-t-o';
import {HalResource} from '@app/share/model/hal/hal';

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  matDialog = inject(MatDialog);
  halFormService = inject(HalFormService);

  constructor() {
  }

  createTeamWithTrainingSessionsAndRoleCoaches(
    team: HalResource,
    teamDtoCreateRequest: CreateTeamDTORequest,
    trainingSessionsDTORequest: FormTrainingSessionDTO[],
    roleCoachesDTORequest: FormRoleCoachDTO[]
  ) {
    return this.createTeam(team, teamDtoCreateRequest).pipe(
      switchMap(team =>
        forkJoin({
          team: of(team),
          trainingResults: this.addTrainingSessions(team, trainingSessionsDTORequest.map(ts => toAddTrainingSessionInTeamDTORequest(ts))),
          roleCoachResults: this.addRoleCoaches(team, roleCoachesDTORequest.map(rc => toAddRoleCoachInTeamDTORequest(rc)))
        }).pipe(
          catchError(error =>
            this.deleteTeam(team).pipe(
              // transmettre l'erreur originale après suppression
              switchMap(() => throwError(() => error))
            )
          )
        )
      ),
    );
  }

  createTeam(
    team: HalResource,
    teamDtoCreateRequest: CreateTeamDTORequest,
  ) {
    if (!this.halFormService.canAction(team, 'createTeam')) {
      throw new Error("L'action createTeam n'est pas disponible sur l'objet " + teamDtoCreateRequest);
    }
    return this.halFormService.doAction<Team>(team, 'createTeam', teamDtoCreateRequest)
  }

  updateTeamWithTrainingSessionsAndRoleCoaches(
    team: Team,
    teamDtoUpdateRequest: CreateTeamDTORequest,
    trainingSessionsDTORequest: FormTrainingSessionDTO[],
    trainingSessionsToDelete: TrainingSession[],
    roleCoachesDTORequest: FormRoleCoachDTO[],
    roleCoachesToDelete: RoleCoach[]
  ) {
    return this.updateTeam(team, teamDtoUpdateRequest).pipe(
      switchMap(team =>
        forkJoin({
          team: of(team),
          trainingResults: this.addTrainingSessions(team, trainingSessionsDTORequest.map(ts => toAddTrainingSessionInTeamDTORequest(ts))),
          roleCoachResults: this.addRoleCoaches(team, roleCoachesDTORequest.map(rc => toAddRoleCoachInTeamDTORequest(rc))),
          deletedTrainingSessions: this.deleteTrainingSessions(trainingSessionsToDelete),
          deletedRoleCoaches: this.deleteRoleCoaches(roleCoachesToDelete)
        }).pipe(
          catchError(error =>
            this.deleteTeam(team).pipe(
              // transmettre l'erreur originale après suppression
              switchMap(() => throwError(() => error))
            )
          )
        )
      )
    )
  }

  updateTeam(team: Team, teamDtoUpdateRequest: CreateTeamDTORequest) {
    if (!this.halFormService.canAction(team, 'updateTeam')) {
      throw new Error("L'action updateTeam n'est pas disponible sur l'objet " + teamDtoUpdateRequest);
    }
    return this.halFormService.doAction<Team>(team, 'updateTeam', teamDtoUpdateRequest);
  }

  deleteTeam(team: Team) {
    if (!this.halFormService.canAction(team, 'deleteTeam')) {
      throw new Error("L'action deleteTeam n'est pas disponible sur l'objet " + team);
    }
    return this.halFormService.doAction<void>(team, 'deleteTeam');
  }


  createDeleteConfirmation(team: Team) {
    const genderDisplay = new GenderPipe().transform(team.gender);
    const categoryDisplay = new CategoryPipe().transform(team.category);

    return this.matDialog.open(ConfirmationDialogComponent, {
      data: {
        title: 'Suppression',
        content: `Etes-vous sur de vouloir supprimer : ${genderDisplay} ${categoryDisplay} ${team.teamNumber}  ?`
      },
    });
  }

  private addTrainingSessions(team: Team, trainingSessionsDTORequest: AddTrainingSessionInTeamDTORequest[]): Observable<TrainingSession[]> {
    if (!this.halFormService.canAction(team, 'addTrainingSession')) {
      throw new Error("L'action addTrainingSession n'est pas disponible sur l'objet " + team);
    }
    const trainingObservables = trainingSessionsDTORequest.map(tsDTORequest =>
      this.halFormService.doAction<TrainingSession>(team, 'addTrainingSession', tsDTORequest)
    );
    return trainingObservables.length ? forkJoin(trainingObservables) : of([]);
  }

  private addRoleCoaches(team: Team, roleCoachesDTORequest: AddRoleCoachInTeamDTORequest[]): Observable<RoleCoach[]> {
    if (!this.halFormService.canAction(team, 'addRoleCoach')) {
      throw new Error("L'action addRoleCoach n'est pas disponible sur l'objet " + team);
    }
    const roleCoachObservables = roleCoachesDTORequest.map(rcDTORequest =>
      this.halFormService.doAction<RoleCoach>(team, 'addRoleCoach', rcDTORequest)
    );
    return roleCoachObservables.length ? forkJoin(roleCoachObservables) : of([]);
  }

  private deleteTrainingSessions(trainingSession: TrainingSession[]) {
    trainingSession.forEach(ts => {
      if (!this.halFormService.canAction(ts, 'deleteTrainingSession')) {
        throw new Error("L'action deleteTrainingSession n'est pas disponible sur l'objet " + ts);
      }
    })
    const trainingSessionObservables = trainingSession.map(ts =>
      this.halFormService.doAction<void>(ts, 'deleteTrainingSession')
    );
    return trainingSessionObservables.length ? forkJoin(trainingSessionObservables) : of([]);
  }

  private deleteRoleCoaches(roleCoach: RoleCoach[]) {
    roleCoach.forEach(rc => {
      if (!this.halFormService.canAction(rc, 'deleteRoleCoach')) {
        throw new Error("L'action deleteRoleCoach n'est pas disponible sur l'objet " + rc);
      }
    })
    const roleCoachObservables = roleCoach.map(rc =>
      this.halFormService.doAction<void>(rc, 'deleteRoleCoach')
    );
    return roleCoachObservables.length ? forkJoin(roleCoachObservables) : of([]);
  }
}
