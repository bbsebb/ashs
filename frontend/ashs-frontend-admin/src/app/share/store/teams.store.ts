import {inject, Injectable, ResourceRef, signal} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {HalFormService, ParamPage} from '@app/share/service/hal-form.service';
import {iif, Observable, of, switchMap} from 'rxjs';
import {AllHalResources, PaginatedHalResource} from '@app/share/model/hal/hal';
import {Team} from '@app/share/model/team';
import {Pagination} from '@app/share/model/hal/pagination';
import {tap} from 'rxjs/operators';
import {CreateTeamDTORequest} from '@app/share/service/dto/create-team-d-t-o-request';
import {TrainingSession} from '@app/share/model/training-session';
import {Coach} from '@app/share/model/coach';
import {RoleCoach} from '@app/share/model/role-coach';
import {FormTrainingSessionDTO} from '@app/share/service/dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '@app/share/service/dto/form-role-coach-d-t-o';
import {TeamService} from '@app/share/service/team.service';
import {NotificationService} from '@app/share/service/notification.service';
import {Router} from '@angular/router';
import {UpdateTeamDTORequest} from '@app/share/service/dto/update-team-d-t-o-request';

/**
 * Store for managing teams data and operations
 * Provides functionality for retrieving, creating, updating, and deleting teams
 */
@Injectable({
  providedIn: 'root'
})
export class TeamsStore {
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 20,
    page: 0
  };
  private readonly halFormService = inject(HalFormService);
  private readonly teamService = inject(TeamService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly _paginationOption = signal<PaginationOption>(TeamsStore.PAGINATION_OPTION_DEFAULT)
  private readonly _teamsResource: ResourceRef<AllHalResources<Team> | PaginatedHalResource<Team> | undefined>;

  /**
   * Initializes the teams resource with pagination options
   */
  constructor() {
    this._teamsResource = rxResource<AllHalResources<Team> | PaginatedHalResource<Team>, PaginationOption>({
      request: () => {
        return this._paginationOption()
      },
      loader: ({request}) => this.halFormService.root.pipe(
        switchMap((root) =>
          iif(
            () => request == 'all',
            this.halFormService.follow<AllHalResources<Team>>(root, "allTeams"),
            this.halFormService.follow<PaginatedHalResource<Team>>(root, "teams", this.buildParamPage(request))
          )
        )
      )
    });
  }

  // ===== GETTERS AND SETTERS =====

  /**
   * Gets the teams resource
   * @returns The teams resource reference
   */
  get teamsResource() {
    return this._teamsResource;
  }

  /**
   * Gets the current pagination option
   * @returns The current pagination option
   */
  get paginationOption() {
    return this._paginationOption();
  }

  /**
   * Sets the pagination option and triggers a resource reload
   * @param paginationOption The new pagination option
   */
  set paginationOption(paginationOption: PaginationOption) {
    this._paginationOption.set(paginationOption);
  }

  // ===== DATA RETRIEVAL METHODS =====

  /**
   * Gets the list of teams from the resource
   * @returns Array of teams or empty array if resource is not available
   */
  getTeams(): Team[] {
    const teamsResource = this._teamsResource.value();
    if (teamsResource) {
      return this.halFormService.unwrap<Team[]>(teamsResource, 'teams')
    }
    return [];
  }

  /**
   * Gets a specific team by URI
   * @param uri The URI of the team to retrieve
   * @returns A resource reference to the team
   */
  getTeam(uri: string) {
    const team = this.getTeams().find(t => t._links.self.href === uri);
    return rxResource({
      request: () => uri,
      loader: ({request}) => team ? of(team) : this.halFormService.loadResource<Team>(request)
    })
  }

  /**
   * Gets the training sessions for a team
   * @param team The team to get training sessions for
   * @returns Array of training sessions or empty array if not available
   */
  getTrainingSessions(team: Team) {
    const trainingSessionResource = rxResource({
      request: () => team,
      loader: ({request}) => this.halFormService.follow<AllHalResources<TrainingSession>>(request, 'trainingSessions')
    }).value();
    if (trainingSessionResource) {
      return this.halFormService.unwrap<TrainingSession[]>(trainingSessionResource, 'trainingSessions')
    }
    return [];
  }

  /**
   * Gets the role coaches for a team
   * @param team The team to get role coaches for
   * @returns Array of role coaches or empty array if not available
   */
  getRoleCoaches(team: Team): RoleCoach[] {
    const roleCoachResource = rxResource({
      request: () => team,
      loader: ({request}) => this.halFormService.follow<AllHalResources<Coach>>(request, 'roleCoaches')
    }).value();
    if (roleCoachResource) {
      return this.halFormService.unwrap<RoleCoach[]>(roleCoachResource, 'roleCoaches')
    }
    return [];
  }

  /**
   * Gets the pagination information from the teams resource
   * @returns Pagination object or undefined if not available
   */
  getPagination(): Pagination | undefined {
    const teamsResource = this._teamsResource.value();
    return this.halFormService.getPagination<Team>(teamsResource);
  }

  // ===== CRUD OPERATIONS =====

  /**
   * Creates a new team with training sessions and role coaches
   * @param teamDtoCreateRequest The team creation request
   * @param trainingSessionsDTORequest The training sessions to add
   * @param roleCoachesDTORequest The role coaches to add
   * @returns Observable that completes when the team is created
   */
  createTeam(
    teamDtoCreateRequest: CreateTeamDTORequest,
    trainingSessionsDTORequest: FormTrainingSessionDTO[],
    roleCoachesDTORequest: FormRoleCoachDTO[]
  ) {
    const teamsResource = this._teamsResource.value();
    if (!teamsResource) {
      throw new Error("Team resource is undefined");
    }
    return this.teamService.createTeamWithTrainingSessionsAndRoleCoaches(teamsResource, teamDtoCreateRequest, trainingSessionsDTORequest, roleCoachesDTORequest).pipe(
      tap(() => this.reloadTeamsResource()), //TODO A optimiser en modifiant directement le store
    );
  }

  /**
   * Updates a team with new data, training sessions, and role coaches
   * @param team The team to update
   * @param updateTeamDTORequest The team update request
   * @param trainingSessions The current training sessions
   * @param formTrainingSessionsDTO The new training sessions
   * @param roleCoaches The current role coaches
   * @param roleCoachesDTORequest The new role coaches
   * @returns Observable that completes when the team is updated
   */
  updateTeam(team: Team,
             updateTeamDTORequest: UpdateTeamDTORequest,
             trainingSessions: TrainingSession[],
             formTrainingSessionsDTO: FormTrainingSessionDTO[],
             roleCoaches: RoleCoach[],
             roleCoachesDTORequest: FormRoleCoachDTO[]) {
    if (!this.halFormService.canAction(team, 'updateTeam')) {
      throw new Error("L'action updateTeam n'est pas disponible sur l'objet " + team);
    }

    // Use the helper methods to identify new and to-be-deleted training sessions
    const newTrainingSessions = this.findNewTrainingSessions(formTrainingSessionsDTO, trainingSessions);
    const trainingSessionsToDelete = this.findTrainingSessionsToDelete(trainingSessions, formTrainingSessionsDTO);

    // Use the helper methods to identify new and to-be-deleted role coaches
    const newRoleCoaches = this.findNewRoleCoaches(roleCoachesDTORequest, roleCoaches);
    const roleCoachesToDelete = this.findRoleCoachesToDelete(roleCoaches, roleCoachesDTORequest);

    return this.teamService.updateTeamWithTrainingSessionsAndRoleCoaches(team, updateTeamDTORequest, newTrainingSessions, trainingSessionsToDelete, newRoleCoaches, roleCoachesToDelete)
      .pipe(
        tap(() => this.reloadTeamsResource()) //TODO A optimiser en modifiant directement le store
      );
  }

  /**
   * Deletes a team
   * @param team The team to delete
   * @returns Observable that completes when the team is deleted
   */
  deleteTeam(team: Team): Observable<void> {
    return this.teamService.deleteTeam(team).pipe(
      tap(() => {
        if (this.getTeams().length === 1) {
          this._paginationOption.update((paginationOption) => {
            if (paginationOption !== 'all') {
              return {size: paginationOption.size, page: paginationOption.page - 1}
            }
            return paginationOption;
          })
        } else {
          this.reloadTeamsResource();
        }
      })
    );
  }

  /**
   * Deletes a team with confirmation dialog
   * @param team The team to delete
   */
  deleteTeamWithConfirmation(team: Team) {
    const matDialogRef = this.teamService.createDeleteConfirmation(team);
    matDialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.deleteTeam(team).subscribe({
          next: () => {
            this.notificationService.showSuccess(`L'équipe a été supprimée`)
            this.router.navigate(['/teams'])
          },
          error: () => this.notificationService.showError('Une erreur est survenue lors de la suppression')
        });
      }
    });
  };

  // ===== HELPER METHODS =====

  /**
   * Checks if a training session matches a form training session based on timeSlot and hall
   * @param trainingSession The existing training session
   * @param formTrainingSession The form training session to compare with
   * @returns true if the sessions match, false otherwise
   */
  private isTrainingSessionMatch(trainingSession: TrainingSession, formTrainingSession: FormTrainingSessionDTO): boolean {
    return trainingSession.timeSlot === formTrainingSession.timeSlot &&
      trainingSession.hall === formTrainingSession.hall;
  }

  /**
   * Identifies new training sessions that don't exist in the current list
   * @param formTrainingSessionsDTO The list of form training sessions
   * @param existingTrainingSessions The list of existing training sessions
   * @returns An array of form training sessions that are new
   */
  private findNewTrainingSessions(
    formTrainingSessionsDTO: FormTrainingSessionDTO[],
    existingTrainingSessions: TrainingSession[]
  ): FormTrainingSessionDTO[] {
    return formTrainingSessionsDTO.filter(formTrainingSession =>
      !existingTrainingSessions.some(trainingSession =>
        this.isTrainingSessionMatch(trainingSession, formTrainingSession)
      )
    );
  }

  /**
   * Identifies training sessions that should be deleted
   * @param existingTrainingSessions The list of existing training sessions
   * @param formTrainingSessionsDTO The list of form training sessions
   * @returns An array of training sessions to delete
   */
  private findTrainingSessionsToDelete(
    existingTrainingSessions: TrainingSession[],
    formTrainingSessionsDTO: FormTrainingSessionDTO[]
  ): TrainingSession[] {
    return existingTrainingSessions.filter(trainingSession =>
      !formTrainingSessionsDTO.some(formTrainingSession =>
        this.isTrainingSessionMatch(trainingSession, formTrainingSession)
      )
    );
  }

  /**
   * Checks if a role coach matches a form role coach based on role and coach
   * @param roleCoach The existing role coach
   * @param formRoleCoach The form role coach to compare with
   * @returns true if the role coaches match, false otherwise
   */
  private isRoleCoachMatch(roleCoach: RoleCoach, formRoleCoach: FormRoleCoachDTO): boolean {
    return roleCoach.role === formRoleCoach.role &&
      roleCoach.coach === formRoleCoach.coach;
  }

  /**
   * Identifies new role coaches that don't exist in the current list
   * @param formRoleCoachesDTO The list of form role coaches
   * @param existingRoleCoaches The list of existing role coaches
   * @returns An array of form role coaches that are new
   */
  private findNewRoleCoaches(
    formRoleCoachesDTO: FormRoleCoachDTO[],
    existingRoleCoaches: RoleCoach[]
  ): FormRoleCoachDTO[] {
    return formRoleCoachesDTO.filter(formRoleCoach =>
      !existingRoleCoaches.some(roleCoach =>
        this.isRoleCoachMatch(roleCoach, formRoleCoach)
      )
    );
  }

  /**
   * Identifies role coaches that should be deleted
   * @param existingRoleCoaches The list of existing role coaches
   * @param formRoleCoachesDTO The list of form role coaches
   * @returns An array of role coaches to delete
   */
  private findRoleCoachesToDelete(
    existingRoleCoaches: RoleCoach[],
    formRoleCoachesDTO: FormRoleCoachDTO[]
  ): RoleCoach[] {
    return existingRoleCoaches.filter(roleCoach =>
      !formRoleCoachesDTO.some(formRoleCoach =>
        this.isRoleCoachMatch(roleCoach, formRoleCoach)
      )
    );
  }

  // ===== PRIVATE UTILITY METHODS =====

  /**
   * Builds the pagination parameters for API requests
   * @param request The pagination option
   * @returns The pagination parameters
   */
  private buildParamPage(request: PaginationOption) {
    let paramPage: ParamPage = {}
    if (request !== 'all') {
      paramPage = {
        page: request.page,
        size: request.size
      }
    }
    return paramPage;
  }

  /**
   * Reloads the teams resource
   */
  private reloadTeamsResource() {
    this._teamsResource.reload()
  }
}

/**
 * Type definition for pagination options
 * Can be either a page/size object or 'all' to retrieve all items
 */
export type PaginationOption = {
  size: number,
  page: number,
} | 'all'
