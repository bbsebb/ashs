import {inject, Injectable} from '@angular/core';

import {catchError, forkJoin, Observable, of, switchMap, throwError} from 'rxjs';
import {AllHalResources, HalResource, NgxHalFormsService, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {NGX_LOGGER} from 'ngx-logger';
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
   * Logger service
   */
  private readonly logger = inject(NGX_LOGGER);

  /**
   * Default pagination options for team requests
   */
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 20,
    page: 0
  };

  constructor() {
    this.logger.debug('TeamService initialized');
  }

  // ===== TEAM CRUD OPERATIONS =====

  /**
   * Retrieves a list of teams with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all teams
   * @returns Observable of paginated or all team resources
   */
  getTeams(paginationOption: PaginationOption = TeamService.PAGINATION_OPTION_DEFAULT): Observable<AllHalResources<Team> | PaginatedHalResource<Team>> {
    this.logger.debug('Getting teams', {paginationOption});

    return this.halFormService.root.pipe(
      switchMap(root => {
        this.logger.debug('Following teams link from root');
        return this.halFormService.follow<PaginatedHalResource<Team>>(root, "teams", this.halFormService.buildParamPage(paginationOption));
      }),
      switchMap((teamsRoot) => {
        if (paginationOption === 'all') {
          this.logger.debug('Getting all teams');
          return this.halFormService.follow<AllHalResources<Team>>(teamsRoot, "allTeams");
        } else {
          this.logger.debug('Using paginated teams', {
            page: paginationOption.page,
            size: paginationOption.size
          });
          return of(teamsRoot);
        }
      })
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
    this.logger.debug('Creating new team', {
      number: teamDtoCreateRequest.teamNumber,
      category: teamDtoCreateRequest.category,
      gender: teamDtoCreateRequest.gender
    });

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
    this.logger.debug('Updating team', {
      teamId: team._links?.self?.href,
      number: teamDtoUpdateRequest.teamNumber,
      category: teamDtoUpdateRequest.category,
      gender: teamDtoUpdateRequest.gender
    });

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
    this.logger.debug('Deleting team', {
      teamId: team._links?.self?.href
    });

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
    this.logger.debug('Creating team with training sessions and role coaches', {
      teamName: `${teamDtoCreateRequest.category}${teamDtoCreateRequest.gender} ${teamDtoCreateRequest.teamNumber}`,
      trainingSessionsCount: trainingSessionsDTORequest.length,
      roleCoachesCount: roleCoachesDTORequest.length
    });

    return this.createTeam(team, teamDtoCreateRequest).pipe(
      switchMap(team => {
        this.logger.debug('Team created, adding training sessions and role coaches', {
          teamId: team._links?.self?.href
        });

        return forkJoin({
          team: of(team),
          trainingResults: this.addTrainingSessions(team, trainingSessionsDTORequest.map(ts => toAddTrainingSessionInTeamDTORequest(ts))),
          roleCoachResults: this.addRoleCoaches(team, roleCoachesDTORequest.map(rc => toAddRoleCoachInTeamDTORequest(rc)))
        }).pipe(
          catchError(error => {
            this.logger.error('Error creating team with training sessions and role coaches, cleaning up', {
              error: error.message,
              teamId: team._links?.self?.href
            });

            return this.deleteTeam(team).pipe(
              // transmettre l'erreur originale après suppression
              switchMap(() => throwError(() => error))
            );
          })
        );
      }),
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
    this.logger.debug('Updating team with training sessions and role coaches', {
      teamId: team._links?.self?.href,
      teamName: `${teamDtoUpdateRequest.category}${teamDtoUpdateRequest.gender} ${teamDtoUpdateRequest.teamNumber}`,
      trainingSessionsToAddCount: trainingSessionsDTORequest.length,
      trainingSessionsToDeleteCount: trainingSessionsToDelete.length,
      roleCoachesToAddCount: roleCoachesDTORequest.length,
      roleCoachesToDeleteCount: roleCoachesToDelete.length
    });

    return this.updateTeam(team, teamDtoUpdateRequest).pipe(
      switchMap(team => {
        this.logger.debug('Team updated, processing training sessions and role coaches', {
          teamId: team._links?.self?.href
        });

        return forkJoin({
          team: of(team),
          trainingResults: this.addTrainingSessions(team, trainingSessionsDTORequest.map(ts => toAddTrainingSessionInTeamDTORequest(ts))),
          roleCoachResults: this.addRoleCoaches(team, roleCoachesDTORequest.map(rc => toAddRoleCoachInTeamDTORequest(rc))),
          deletedTrainingSessions: this.deleteTrainingSessions(trainingSessionsToDelete),
          deletedRoleCoaches: this.deleteRoleCoaches(roleCoachesToDelete)
        }).pipe(
          catchError(error => {
            this.logger.error('Error updating team with training sessions and role coaches', {
              error: error.message,
              teamId: team._links?.self?.href
            });

            return this.deleteTeam(team).pipe(
              // transmettre l'erreur originale après suppression
              switchMap(() => throwError(() => error))
            );
          })
        );
      })
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
    this.logger.debug('Getting training sessions for team', {
      teamId: team._links?.self?.href
    });

    if (!this.halFormService.hasFollow(team, 'trainingSessionsList')) {
      this.logger.debug('No training sessions link found for team', {
        teamId: team._links?.self?.href
      });
      return of([])
    }

    return this.halFormService
      .follow<TrainingSession[]>(team, 'trainingSessionsList')
      .pipe(
        catchError(error => {
          this.logger.error('Error getting training sessions', {
            error: error.message,
            teamId: team._links?.self?.href
          });
          return of([]); // en cas d'erreur, renvoyer []
        })
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
    this.logger.debug('Adding training sessions to team', {
      teamId: team._links?.self?.href,
      count: trainingSessionsDTORequest.length
    });

    if (trainingSessionsDTORequest.length === 0) {
      this.logger.debug('No training sessions to add');
      return of([]);
    }

    const trainingObservables = trainingSessionsDTORequest.map(tsDTORequest => {
      this.logger.debug('Adding training session', {
        dayOfWeek: tsDTORequest.trainingSessionDTORequest.timeSlot.dayOfWeek,
        startTime: tsDTORequest.trainingSessionDTORequest.timeSlot.startTime,
        endTime: tsDTORequest.trainingSessionDTORequest.timeSlot.endTime,
        hallId: tsDTORequest.hallId
      });

      return this.halFormService.doAction<TrainingSession>(team, 'addTrainingSession', tsDTORequest);
    });

    return forkJoin(trainingObservables);
  }

  /**
   * Deletes training sessions
   *
   * @param trainingSessions - Array of training sessions to delete
   * @returns Observable of deletion results
   * @throws Error if the deleteTrainingSession action is not available for any session
   */
  private deleteTrainingSessions(trainingSessions: TrainingSession[]) {
    this.logger.debug('Deleting training sessions', {
      count: trainingSessions.length
    });

    if (trainingSessions.length === 0) {
      this.logger.debug('No training sessions to delete');
      return of([]);
    }

    // Check if all training sessions can be deleted
    for (const ts of trainingSessions) {
      if (!this.halFormService.canAction(ts, 'deleteTrainingSession')) {
        const errorMsg = "L'action deleteTrainingSession n'est pas disponible sur l'objet " + ts;
        this.logger.error('Cannot delete training session - action not available', {
          trainingSessionId: ts._links?.self?.href,
          dayOfWeek: ts.timeSlot.dayOfWeek,
          startTime: ts.timeSlot.startTime
        });
        return throwError(() => new Error(errorMsg));
      }
    }

    // Delete all training sessions
    const trainingSessionObservables = trainingSessions.map(ts => {
      this.logger.debug('Deleting training session', {
        trainingSessionId: ts._links?.self?.href,
        dayOfWeek: ts.timeSlot.dayOfWeek,
        startTime: ts.timeSlot.startTime
      });

      return this.halFormService.doAction<void>(ts, 'deleteTrainingSession');
    });

    return forkJoin(trainingSessionObservables);
  }

  // ===== ROLE COACH OPERATIONS =====

  /**
   * Retrieves role coaches associated with a team
   *
   * @param team - The team to get role coaches for
   * @returns Observable of role coaches array
   */
  getRoleCoaches(team: Team): Observable<RoleCoach[]> {
    this.logger.debug('Getting role coaches for team', {
      teamId: team._links?.self?.href
    });

    if (!this.halFormService.hasFollow(team, 'roleCoachesList')) {
      this.logger.debug('No role coaches link found for team', {
        teamId: team._links?.self?.href
      });
      return of([])
    }

    return this.halFormService
      .follow<RoleCoach[]>(team, 'roleCoachesList')
      .pipe(
        catchError(error => {
          this.logger.error('Error getting role coaches', {
            error: error.message,
            teamId: team._links?.self?.href
          });
          return of([]); // en cas d'erreur, renvoyer []
        })
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
    this.logger.debug('Adding role coaches to team', {
      teamId: team._links?.self?.href,
      count: roleCoachesDTORequest.length
    });

    if (roleCoachesDTORequest.length === 0) {
      this.logger.debug('No role coaches to add');
      return of([]);
    }

    const roleCoachObservables = roleCoachesDTORequest.map(rcDTORequest => {
      this.logger.debug('Adding role coach', {
        coachId: rcDTORequest.coachId,
        role: rcDTORequest.role
      });

      return this.halFormService.doAction<RoleCoach>(team, 'addRoleCoach', rcDTORequest);
    });

    return forkJoin(roleCoachObservables);
  }

  /**
   * Deletes role coaches
   *
   * @param roleCoach - Array of role coaches to delete
   * @returns Observable of deletion results
   * @throws Error if the deleteRoleCoach action is not available for any coach
   */
  private deleteRoleCoaches(roleCoach: RoleCoach[]) {
    this.logger.debug('Deleting role coaches', {
      count: roleCoach.length
    });

    if (roleCoach.length === 0) {
      this.logger.debug('No role coaches to delete');
      return of([]);
    }

    // Check if all role coaches can be deleted
    for (const rc of roleCoach) {
      if (!this.halFormService.canAction(rc, 'deleteRoleCoach')) {
        const errorMsg = "L'action deleteRoleCoach n'est pas disponible sur l'objet " + rc;
        this.logger.error('Cannot delete role coach - action not available', {
          roleCoachId: rc._links?.self?.href,
          role: rc.role
        });
        return throwError(() => new Error(errorMsg));
      }
    }

    // Delete all role coaches
    const roleCoachObservables = roleCoach.map(rc => {
      this.logger.debug('Deleting role coach', {
        roleCoachId: rc._links?.self?.href,
        role: rc.role,
        coachId: rc.coach?._links?.self?.href
      });

      return this.halFormService.doAction<void>(rc, 'deleteRoleCoach');
    });

    return forkJoin(roleCoachObservables);
  }

  /**
   * Retrieves a team by its URI
   *
   * @param uri - The URI of the team to retrieve
   * @returns Observable of the team
   */
  getTeam(uri: string): Observable<Team> {
    this.logger.debug('Getting team by URI', {uri});

    return this.halFormService.loadResource<Team>(uri);
  }
}
