import {Observable} from 'rxjs';
import {AllHalResources, HalResource, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Team} from '../model/team';
import {CreateTeamDTORequest} from '../dto/create-team-d-t-o-request';
import {FormTrainingSessionDTO} from '../dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '../dto/form-role-coach-d-t-o';
import {TrainingSession} from '../model/training-session';
import {RoleCoach} from '../model/role-coach';
import {InjectionToken} from '@angular/core';

/**
 * Interface for team service operations.
 * Defines contract for CRUD operations on teams and related entities (training sessions, role coaches)
 * using HAL-FORMS API.
 */
export interface ITeamService {
  /**
   * Retrieves a list of teams with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all teams
   * @returns Observable of paginated or all team resources
   */
  getTeams(paginationOption?: PaginationOption): Observable<AllHalResources<Team> | PaginatedHalResource<Team>>;

  /**
   * Creates a new team
   *
   * @param team - The team resource collection to add to
   * @param teamDtoCreateRequest - Data for the new team
   * @returns Observable of the created team
   * @throws Error if the createTeam action is not available
   */
  createTeam(team: HalResource, teamDtoCreateRequest: CreateTeamDTORequest): Observable<Team>;

  /**
   * Updates an existing team
   *
   * @param team - The team to update
   * @param teamDtoUpdateRequest - Updated team data
   * @returns Observable of the updated team
   * @throws Error if the updateTeam action is not available
   */
  updateTeam(team: Team, teamDtoUpdateRequest: CreateTeamDTORequest): Observable<Team>;

  /**
   * Deletes a team
   *
   * @param team - The team to delete
   * @returns Observable of void
   * @throws Error if the deleteTeam action is not available
   */
  deleteTeam(team: Team): Observable<void>;

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
  ): Observable<{
    team: Team;
    trainingResults: TrainingSession[];
    roleCoachResults: RoleCoach[];
  }>;

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
  ): Observable<{
    team: Team;
    trainingResults: TrainingSession[];
    roleCoachResults: RoleCoach[];
    deletedTrainingSessions: any[];
    deletedRoleCoaches: any[];
  }>;

  /**
   * Retrieves training sessions associated with a team
   *
   * @param team - The team to get training sessions for
   * @returns Observable of training sessions array
   */
  getTrainingSessions(team: Team): Observable<TrainingSession[]>;

  /**
   * Retrieves role coaches associated with a team
   *
   * @param team - The team to get role coaches for
   * @returns Observable of role coaches array
   */
  getRoleCoaches(team: Team): Observable<RoleCoach[]>;

  /**
   * Retrieves a team by its URI
   *
   * @param uri - The URI of the team to retrieve
   * @returns Observable of the team
   */
  getTeam(uri: string): Observable<Team>;
}

export const TEAM_SERVICE = new InjectionToken<ITeamService>('TeamService');
