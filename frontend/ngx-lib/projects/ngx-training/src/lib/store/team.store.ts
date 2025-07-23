import {inject, Injectable, signal} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {of} from 'rxjs';
import {tap} from 'rxjs/operators';
import {TeamsStore} from './teams.store';
import {UpdateTeamDTORequest} from '../dto/update-team-d-t-o-request';
import {FormTrainingSessionDTO} from '../dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '../dto/form-role-coach-d-t-o';
import {TEAM_SERVICE} from '../service/i-team.service';

/**
 * Store for managing a single team's data and operations
 * Provides functionality for retrieving and updating a team
 */
@Injectable({
  providedIn: 'root'
})
export class TeamStore {
  private readonly teamService = inject(TEAM_SERVICE);
  private readonly _uri = signal<string | undefined>(undefined);
  private readonly _teamResource;
  private readonly _trainingSessionResource;
  private readonly _roleCoachResource;
  private readonly teamsStore = inject(TeamsStore);

  /**
   * Initializes the team resources
   * Sets up the team, training sessions, and role coaches resources
   */
  constructor() {
    this._teamResource = this.loadTeam();

    this._trainingSessionResource = this.loadTrainingSessions();
    this._roleCoachResource = this.loadRoleCoaches();
  }

  loadTeam() {
    return rxResource({
      request: () => {
        const uri = this._uri();
        if (uri) {
          return decodeURIComponent(uri);
        }
        return undefined
      },
      loader: ({request}) => {
        const team = this.teamsStore.getTeamByUri(request)();
        return team ? of(team) : this.teamService.getTeam(request);
      }
    });
  }

  loadTrainingSessions() {
    return rxResource({
      request: () => this.teamResource.value(),
      loader: ({request}) => {
        return request ? this.teamService.getTrainingSessions(request) : of([]);
      }
    });
  }

  loadRoleCoaches() {
    return rxResource({
      request: () => this.teamResource.value(),
      loader: ({request}) => {
        return request ? this.teamService.getRoleCoaches(request) : of([]);
      }
    });
  }

  /**
   * Gets the team resource
   * @returns The team resource reference
   */
  private get teamResource() {
    return this._teamResource;
  }

  get team() {
    return this.teamResource.value;
  }

  reloadTeam() {
    this.teamResource.reload();
  }

  get teamResourceIsLoading() {
    return this.teamResource.isLoading;
  }

  get teamResourceStatus() {
    return this.teamResource.status;
  }

  get teamResourceError() {
    return this.teamResource.error;
  }

  /**
   * Sets the URI and triggers a resource reload
   * @param uri The new URI
   */
  set uri(uri: string | undefined) {
    this._uri.set(uri);
  }

  /**
   * Gets the training session resource
   * @returns The training session resource reference
   */
  get trainingSessionResource() {
    return this._trainingSessionResource;
  }

  get trainingSession() {
    return this.trainingSessionResource.value;
  }

  reloadTrainingSession() {
    this.trainingSessionResource.reload();
  }

  get trainingSessionResourceIsLoading() {
    return this.trainingSessionResource.isLoading;
  }

  get trainingSessionResourceStatus() {
    return this.trainingSessionResource.status;
  }

  get trainingSessionResourceError() {
    return this.trainingSessionResource.error;
  }

  /**
   * Gets the role coach resource
   * @returns The role coach resource reference
   */
  private get roleCoachResource() {
    return this._roleCoachResource;
  }

  get roleCoach() {
    return this.roleCoachResource.value;
  }


  reloadRoleCoach() {
    this.roleCoachResource.reload();
  }

  get roleCoachResourceIsLoading() {
    return this.roleCoachResource.isLoading;
  }

  get roleCoachResourceStatus() {
    return this.roleCoachResource.status;
  }

  get roleCoachResourceError() {
    return this.roleCoachResource.error;
  }

  /**
   * Updates a team with new data, training sessions, and role coaches
   * @param updateTeamDTORequest The team update request
   * @param formTrainingSessionsDTO The new training sessions
   * @param roleCoachesDTORequest The new role coaches
   * @returns Observable that completes when the team is updated
   */
  updateTeam(updateTeamDTORequest: UpdateTeamDTORequest,
             formTrainingSessionsDTO: FormTrainingSessionDTO[],
             roleCoachesDTORequest: FormRoleCoachDTO[]) {
    const team = this.teamResource.value();
    if (!team) {
      throw new Error("Team resource is undefined");
    }
    const trainingSessions = this.trainingSessionResource.value() ?? [];
    const roleCoaches = this.roleCoachResource.value() ?? [];

    return this.teamsStore.updateTeam(team, updateTeamDTORequest, trainingSessions, formTrainingSessionsDTO, roleCoaches, roleCoachesDTORequest)
      .pipe(
        // Directly update the store resources instead of reloading
        tap((result) => {
          // Update team resource directly with the updated team
          this.teamResource.update(() => result.team);

          // Calculate the updated training sessions by removing deleted ones and adding new ones
          this.trainingSessionResource.update((currentTrainingSessions) => {
            // Filter out deleted training sessions
            const filteredTrainingSessions = currentTrainingSessions?.filter(ts =>
              !result.deletedTrainingSessions.some(deleted =>
                deleted._links?.self?.href === ts._links?.self?.href
              )
            ) ?? [];

            // Add new training sessions
            return [...filteredTrainingSessions, ...result.trainingResults];
          });

          // Calculate the updated role coaches by removing deleted ones and adding new ones
          this.roleCoachResource.update((currentRoleCoaches) => {
            // Filter out deleted role coaches
            const filteredRoleCoaches = currentRoleCoaches?.filter(rc =>
              !result.deletedRoleCoaches.some(deleted =>
                deleted._links?.self?.href === rc._links?.self?.href
              )
            ) ?? [];

            // Add new role coaches
            return [...filteredRoleCoaches, ...result.roleCoachResults];
          });
        })
      );
  }


}
