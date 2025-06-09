import {inject, Injectable} from '@angular/core';

import {catchError, forkJoin, iif, Observable, of, switchMap, throwError} from 'rxjs';
import {AllHalResources, HalResource, NgxHalFormsService, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Team} from '../model/team';
import {CreateTeamDTORequest} from '../dto/create-team-d-t-o-request';
import {FormTrainingSessionDTO} from '../dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '../dto/form-role-coach-d-t-o';
import {
  AddTrainingSessionInTeamDTORequest,
  toAddTrainingSessionInTeamDTORequest
} from '../dto/add-training-session-in-team-d-t-o-request';
import {
  AddRoleCoachInTeamDTORequest,
  toAddRoleCoachInTeamDTORequest
} from '../dto/add-role-coach-in-team-d-t-o-request';
import {TrainingSession} from '../model/training-session';
import {RoleCoach} from '../model/role-coach';
import {ITeamService} from './i-team.service';

/**
 * Service for managing team resources.
 * Provides CRUD operations for teams and related entities (training sessions, role coaches)
 * using HAL-FORMS API.
 */
@Injectable({
  providedIn: 'root',
})
export class TeamService implements ITeamService {
  /**
   * HAL Forms service for API communication
   */
  halFormService = inject(NgxHalFormsService);

  /**
   * Default pagination options for team requests
   */
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 20,
    page: 0
  };

  constructor() {
  }

  // ===== TEAM CRUD OPERATIONS =====

  /**
   * Retrieves a list of teams with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all teams
   * @returns Observable of paginated or all team resources
   */
  getTeams(paginationOption: PaginationOption = TeamService.PAGINATION_OPTION_DEFAULT): Observable<AllHalResources<Team> | PaginatedHalResource<Team>> {
    return this.halFormService.root.pipe(
      switchMap((root) =>
        iif(
          () => paginationOption == 'all',
          this.halFormService.follow<AllHalResources<Team>>(root, "allTeams"),
          this.halFormService.follow<PaginatedHalResource<Team>>(root, "teams", this.halFormService.buildParamPage(paginationOption))
        )
      )
    );
  }

  /**
   * Creates a new team
   *
   * @param team - The team resource collection to add to
   * @param teamDtoCreateRequest - Data for the new team
   * @returns Observable of the created team
   * @throws Error if the createTeam action is not available
   */
  createTeam(
    team: HalResource,
    teamDtoCreateRequest: CreateTeamDTORequest,
  ) {
    if (!this.halFormService.canAction(team, 'createTeam')) {
      throw new Error("L'action createTeam n'est pas disponible sur l'objet " + teamDtoCreateRequest);
    }
    return this.halFormService.doAction<Team>(team, 'createTeam', teamDtoCreateRequest)
  }

  /**
   * Updates an existing team
   *
   * @param team - The team to update
   * @param teamDtoUpdateRequest - Updated team data
   * @returns Observable of the updated team
   * @throws Error if the updateTeam action is not available
   */
  updateTeam(team: Team, teamDtoUpdateRequest: CreateTeamDTORequest) {
    if (!this.halFormService.canAction(team, 'updateTeam')) {
      throw new Error("L'action updateTeam n'est pas disponible sur l'objet " + teamDtoUpdateRequest);
    }
    return this.halFormService.doAction<Team>(team, 'updateTeam', teamDtoUpdateRequest);
  }

  /**
   * Deletes a team
   *
   * @param team - The team to delete
   * @returns Observable of void
   * @throws Error if the deleteTeam action is not available
   */
  deleteTeam(team: Team) {
    if (!this.halFormService.canAction(team, 'deleteTeam')) {
      throw new Error("L'action deleteTeam n'est pas disponible sur l'objet " + team);
    }
    return this.halFormService.doAction<void>(team, 'deleteTeam');
  }

  // ===== COMPOSITE OPERATIONS =====

  /**
   * Creates a team with associated training sessions and role coaches in a single operation
   *
   * @param team - The team resource collection to add to
   * @param teamDtoCreateRequest - Data for the new team
   * @param trainingSessionsDTORequest - Training sessions to add to the team
   * @param roleCoachesDTORequest - Role coaches to add to the team
   * @returns Observable with the created team and associated entities
   */
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

  /**
   * Updates a team with associated training sessions and role coaches in a single operation
   *
   * @param team - The team to update
   * @param teamDtoUpdateRequest - Updated team data
   * @param trainingSessionsDTORequest - Training sessions to add to the team
   * @param trainingSessionsToDelete - Training sessions to remove from the team
   * @param roleCoachesDTORequest - Role coaches to add to the team
   * @param roleCoachesToDelete - Role coaches to remove from the team
   * @returns Observable with the updated team and associated entities
   * @throws Error if the updateTeam action is not available
   */
  updateTeamWithTrainingSessionsAndRoleCoaches(
    team: Team,
    teamDtoUpdateRequest: CreateTeamDTORequest,
    trainingSessionsDTORequest: FormTrainingSessionDTO[],
    trainingSessionsToDelete: TrainingSession[],
    roleCoachesDTORequest: FormRoleCoachDTO[],
    roleCoachesToDelete: RoleCoach[]
  ) {
    if (!this.halFormService.canAction(team, 'updateTeam')) {
      throw new Error("L'action updateTeam n'est pas disponible sur l'objet " + team);
    }
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

  // ===== TRAINING SESSION OPERATIONS =====

  /**
   * Retrieves training sessions associated with a team
   *
   * @param team - The team to get training sessions for
   * @returns Observable of training sessions array
   */
  getTrainingSessions(team: Team): Observable<TrainingSession[]> {
    if (!this.halFormService.hasFollow(team, 'trainingSessionsList')) {
      return of([])
    }
    return this.halFormService
      .follow<TrainingSession[]>(team, 'trainingSessionsList')
      .pipe(
        catchError(_ => of([])) // en cas d'erreur, renvoyer []
      );
  }

  /**
   * Adds training sessions to a team
   *
   * @param team - The team to add training sessions to
   * @param trainingSessionsDTORequest - Training sessions to add
   * @returns Observable of added training sessions
   * @throws Error if the addTrainingSession action is not available
   */
  private addTrainingSessions(team: Team, trainingSessionsDTORequest: AddTrainingSessionInTeamDTORequest[]): Observable<TrainingSession[]> {
    if (!this.halFormService.canAction(team, 'addTrainingSession')) {
      throw new Error("L'action addTrainingSession n'est pas disponible sur l'objet " + team);
    }
    const trainingObservables = trainingSessionsDTORequest.map(tsDTORequest =>
      this.halFormService.doAction<TrainingSession>(team, 'addTrainingSession', tsDTORequest)
    );
    return trainingObservables.length ? forkJoin(trainingObservables) : of([]);
  }

  /**
   * Deletes training sessions
   *
   * @param trainingSession - Array of training sessions to delete
   * @returns Observable of deletion results
   * @throws Error if the deleteTrainingSession action is not available for any session
   */
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

  // ===== ROLE COACH OPERATIONS =====

  /**
   * Retrieves role coaches associated with a team
   *
   * @param team - The team to get role coaches for
   * @returns Observable of role coaches array
   */
  getRoleCoaches(team: Team): Observable<RoleCoach[]> {
    if (!this.halFormService.hasFollow(team, 'roleCoachesList')) {
      return of([])
    }
    return this.halFormService
      .follow<RoleCoach[]>(team, 'roleCoachesList')
      .pipe(
        catchError(_ => of([])) // en cas d'erreur, renvoyer []
      );
  }

  /**
   * Adds role coaches to a team
   *
   * @param team - The team to add role coaches to
   * @param roleCoachesDTORequest - Role coaches to add
   * @returns Observable of added role coaches
   * @throws Error if the addRoleCoach action is not available
   */
  private addRoleCoaches(team: Team, roleCoachesDTORequest: AddRoleCoachInTeamDTORequest[]): Observable<RoleCoach[]> {
    if (!this.halFormService.canAction(team, 'addRoleCoach')) {
      throw new Error("L'action addRoleCoach n'est pas disponible sur l'objet " + team);
    }
    const roleCoachObservables = roleCoachesDTORequest.map(rcDTORequest =>
      this.halFormService.doAction<RoleCoach>(team, 'addRoleCoach', rcDTORequest)
    );
    return roleCoachObservables.length ? forkJoin(roleCoachObservables) : of([]);
  }

  /**
   * Deletes role coaches
   *
   * @param roleCoach - Array of role coaches to delete
   * @returns Observable of deletion results
   * @throws Error if the deleteRoleCoach action is not available for any coach
   */
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

  /**
   * Retrieves a team by its URI
   *
   * @param uri - The URI of the team to retrieve
   * @returns Observable of the team
   */
  getTeam(uri: string): Observable<Team> {
    return this.halFormService.loadResource<Team>(uri);
  }
}
