import {computed, inject, Injectable, ResourceRef, Signal, signal} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {Observable} from 'rxjs';
import {
  addItemInEmbedded,
  AllHalResources,
  deleteItemInEmbedded,
  getPagination,
  PaginatedHalResource,
  PaginationOption,
  setItemInEmbedded,
  unwrap
} from 'ngx-hal-forms';

import {tap} from 'rxjs/operators';
import {Team} from '../model/team';
import {CreateTeamDTORequest} from '../dto/create-team-d-t-o-request';
import {FormTrainingSessionDTO} from '../dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '../dto/form-role-coach-d-t-o';
import {UpdateTeamDTORequest} from '../dto/update-team-d-t-o-request';
import {TrainingSession} from '../model/training-session';
import {RoleCoach} from '../model/role-coach';
import {TEAM_SERVICE} from '../service/i-team.service';


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
  private readonly teamService = inject(TEAM_SERVICE);

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
      loader: ({request}) => this.teamService.getTeams(request)
    });
  }

  // ===== GETTERS AND SETTERS =====

  /**
   * Gets the teams resource
   * @returns The teams resource reference
   */
  private get teamsResource() {
    return this._teamsResource;
  }

  get teamsHalResource() {
    return this.teamsResource.value;
  }

  /**
   * Gets the list of teams from the resource
   * @returns Array of teams or empty array if resource is not available
   */
  get teams(): Signal<Team[]> {
    return computed(() => {
      const teamsHalResource = this.teamsHalResource();
      if (teamsHalResource) {
        return unwrap<Team[]>(teamsHalResource, 'teams')
      }
      return [];
    })
  }

  getTeamByUri(uri: string) {
    return computed(() => this.teams().find(t => t._links.self.href === uri));
  }


  teamsResourceIsLoading() {
    return this.teamsResource.isLoading();
  }

  getTeamsResourceStatus() {
    return this.teamsResource.status();
  }

  getTeamsResourceError() {
    return this.teamsResource.error();
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
   * Gets the pagination information from the teams resource
   * @returns Pagination object or undefined if not available
   */
  get pagination() {
    return computed(() => getPagination<Team>(this.teamsHalResource()))
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
    const teamsResource = this.teamsHalResource();
    if (!teamsResource) {
      throw new Error("Team resource is undefined");
    }
    return this.teamService.createTeamWithTrainingSessionsAndRoleCoaches(teamsResource, teamDtoCreateRequest, trainingSessionsDTORequest, roleCoachesDTORequest).pipe(
      tap((res) => this.teamsResource.update(teamsResource => addItemInEmbedded(teamsResource, 'teams', res.team))),
      tap(() => this.reloadTeamsResource()),
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


    // Use the helper methods to identify new and to-be-deleted training sessions
    const newTrainingSessions = this.findNewTrainingSessions(formTrainingSessionsDTO, trainingSessions);
    const trainingSessionsToDelete = this.findTrainingSessionsToDelete(trainingSessions, formTrainingSessionsDTO);

    // Use the helper methods to identify new and to-be-deleted role coaches
    const newRoleCoaches = this.findNewRoleCoaches(roleCoachesDTORequest, roleCoaches);
    const roleCoachesToDelete = this.findRoleCoachesToDelete(roleCoaches, roleCoachesDTORequest);

    return this.teamService.updateTeamWithTrainingSessionsAndRoleCoaches(team, updateTeamDTORequest, newTrainingSessions, trainingSessionsToDelete, newRoleCoaches, roleCoachesToDelete)
      .pipe(
        tap((res) => this.teamsResource.update(teamsResource => setItemInEmbedded(teamsResource, 'teams', res.team))),
        tap(() => this.reloadTeamsResource())
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
        if (this.teams().length === 1) {
          this.goToPreviousPage()
        } else {
          this.teamsResource.update((teamsResource) => deleteItemInEmbedded(teamsResource, 'teams', team));
        }
      }),
      tap(() => this.reloadTeamsResource())
    );
  }


  // ===== HELPER METHODS =====

  goToPreviousPage() {
    this._paginationOption.update((paginationOption) => {
      if (paginationOption !== 'all') {
        return {size: paginationOption.size, page: paginationOption.page - 1}
      }
      return paginationOption;
    })
  }

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
   * Reloads the teams resource
   */
  private reloadTeamsResource() {
    this._teamsResource.reload()
  }
}

