import {inject, Injectable, signal} from '@angular/core';
import {HalFormService} from '@app/share/service/hal-form.service';
import {rxResource} from '@angular/core/rxjs-interop';
import {Team} from '@app/share/model/team';
import {TrainingSession} from '@app/share/model/training-session';
import {of} from 'rxjs';
import {RoleCoach} from '@app/share/model/role-coach';
import {tap} from 'rxjs/operators';
import {UpdateTeamDTORequest} from '@app/share/service/dto/update-team-d-t-o-request';
import {FormTrainingSessionDTO} from '@app/share/service/dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '@app/share/service/dto/form-role-coach-d-t-o';
import {TeamsStore} from '@app/share/store/teams.store';

/**
 * Store for managing a single team's data and operations
 * Provides functionality for retrieving and updating a team
 */
@Injectable({
  providedIn: 'root'
})
export class TeamStore {
  private readonly halFormService = inject(HalFormService);
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
        const team = this.teamsStore.getTeamByUri(request);
        return team ? of(team) : this.halFormService.loadResource<Team>(request);
      }
    });
  }

  loadTrainingSessions() {
    return rxResource({
      request: () => this.teamResource.value(),
      loader: ({request}) => {
        return request ? this.halFormService.follow<TrainingSession[]>(request, 'trainingSessionsList') : of([]);
      }
    });
  }

  loadRoleCoaches() {
    return rxResource({
      request: () => this.teamResource.value(),
      loader: ({request}) => {
        return request ? this.halFormService.follow<RoleCoach[]>(request, 'roleCoachesList') : of([]);
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

  getTeam() {
    return this.teamResource.value();
  }

  reloadTeam() {
    this.teamResource.reload();
  }

  teamResourceIsLoading() {
    return this.teamResource.isLoading();
  }

  getTeamResourceStatus() {
    return this.teamResource.status();
  }

  getTeamResourceError() {
    return this.teamResource.error();
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

  getTrainingSession() {
    return this.trainingSessionResource.value();
  }

  reloadTrainingSession() {
    this.trainingSessionResource.reload();
  }

  trainingSessionResourceIsLoading() {
    return this.trainingSessionResource.isLoading();
  }

  getTrainingSessionResourceStatus() {
    return this.trainingSessionResource.status();
  }

  getTrainingSessionResourceError() {
    return this.trainingSessionResource.error();
  }

  /**
   * Gets the role coach resource
   * @returns The role coach resource reference
   */
  private get roleCoachResource() {
    return this._roleCoachResource;
  }

  getRoleCoach() {
    return this.roleCoachResource.value();
  }

  reloadRoleCoach() {
    this.roleCoachResource.reload();
  }

  roleCoachResourceIsLoading() {
    return this.roleCoachResource.isLoading();
  }

  getRoleCoachResourceStatus() {
    return this.roleCoachResource.status();
  }

  getRoleCoachResourceError() {
    return this.roleCoachResource.error();
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
        tap(() => this.teamResource.reload()), //TODO A optimiser en modifiant directement le store
      );
  }


}
