import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {AllHalResources, HalLinkBuilder, HalResource, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Team, TeamBuilder} from '../../model/team';
import {CreateTeamDTORequest} from '../../dto/create-team-d-t-o-request';
import {FormTrainingSessionDTO} from '../../dto/form-training-session-d-t-o';
import {FormRoleCoachDTO} from '../../dto/form-role-coach-d-t-o';
import {TrainingSession, TrainingSessionBuilder} from '../../model/training-session';
import {RoleCoach, RoleCoachBuilder} from '../../model/role-coach';
import {ITeamService} from '../i-team.service';
import {toAddTrainingSessionInTeamDTORequest} from '../../dto/add-training-session-in-team-d-t-o-request';
import {toAddRoleCoachInTeamDTORequest} from '../../dto/add-role-coach-in-team-d-t-o-request';
import {Gender} from '../../model/gender';
import {Category} from '../../model/category';
import {Role} from '../../model/role';
import {DayOfWeek} from '../../model/day-of-week';
import {TimeSlotBuilder} from '../../model/time-slot';
import {HallBuilder} from '../../model/hall';
import {CoachBuilder} from '../../model/coach';
import {AddressBuilder} from '../../model/address';

/**
 * Stub implementation of TeamService for testing purposes.
 * This implementation doesn't use halFormService but uses the Team object type
 * that extends HalResource.
 */
@Injectable({
  providedIn: 'root',
})
export class TeamStubService implements ITeamService {
  // Mock data
  private teams: Team[] = [];
  private trainingSessions: Map<string, TrainingSession[]> = new Map();
  private roleCoaches: Map<string, RoleCoach[]> = new Map();
  private nextId = 1;

  constructor() {
    // Initialize with some mock data
    this.init();
  }

  /**
   * Initializes the stub with two teams
   */
  init(): void {
    // Create first team
    const id1 = this.nextId++;
    const team1 = new TeamBuilder()
      .id(id1)
      .gender(Gender.Masculine)
      .category(Category.SENIOR)
      .teamNumber(1)
      .withLinks(builder => {
        return builder.selfUrl(`/teams/${id1}`);
      })
      .build();

    // Create second team
    const id2 = this.nextId++;
    const team2 = new TeamBuilder()
      .id(id2)
      .gender(Gender.Feminine)
      .category(Category.U15)
      .teamNumber(2)
      .withLinks(builder => {
        return builder.selfUrl(`/teams/${id2}`);
      })
      .build();

    // Add teams to the array
    this.teams.push(team1, team2);

    // Initialize training sessions and role coaches collections for each team
    this.trainingSessions.set(team1._links.self.href, []);
    this.roleCoaches.set(team1._links.self.href, []);
    this.trainingSessions.set(team2._links.self.href, []);
    this.roleCoaches.set(team2._links.self.href, []);

    // Create training sessions for team1
    this.createTrainingSessionsForTeam(team1);

    // Create training sessions for team2
    this.createTrainingSessionsForTeam(team2);

    // Create role coach for team1
    this.createRoleCoachForTeam(team1);

    // Create role coach for team2
    this.createRoleCoachForTeam(team2);
  }

  /**
   * Creates two training sessions for a team
   * @param team The team to create training sessions for
   */
  private createTrainingSessionsForTeam(team: Team): void {
    // Create addresses for halls
    const address1 = new AddressBuilder()
      .street("123 Main St")
      .city("New York")
      .postalCode("10001")
      .country("USA")
      .build();

    const address2 = new AddressBuilder()
      .street("456 Park Ave")
      .city("Los Angeles")
      .postalCode("90001")
      .country("USA")
      .build();

    // Create halls using HallBuilder
    const hall1 = new HallBuilder()
      .id(1)
      .name("Main Hall")
      .address(address1)
      .withLinks(builder => {
        return builder.selfUrl(`/halls/1`);
      })
      .build();

    const hall2 = new HallBuilder()
      .id(2)
      .name("Secondary Hall")
      .address(address2)
      .withLinks(builder => {
        return builder.selfUrl(`/halls/2`);
      })
      .build();

    // Create first training session
    const tsId1 = this.nextId++;
    const timeSlot1 = new TimeSlotBuilder()
      .id(tsId1)
      .dayOfWeek(DayOfWeek.MONDAY)
      .startTime("18:00")
      .endTime("20:00")
      .build();

    const trainingSession1 = new TrainingSessionBuilder()
      .id(tsId1)
      .timeSlot(timeSlot1)
      .hall(hall1)
      .withLinks(builder => {
        return builder
          .selfUrl(`/trainingSessions/${tsId1}`)
          .link('team', new HalLinkBuilder().href(team._links.self.href).build());
      })
      .template('deleteTrainingSession', {
        key: 'deleteTrainingSession',
        method: 'DELETE',
        properties: []
      })
      .build();

    // Create second training session
    const tsId2 = this.nextId++;
    const timeSlot2 = new TimeSlotBuilder()
      .id(tsId2)
      .dayOfWeek(DayOfWeek.WEDNESDAY)
      .startTime("19:00")
      .endTime("21:00")
      .build();

    const trainingSession2 = new TrainingSessionBuilder()
      .id(tsId2)
      .timeSlot(timeSlot2)
      .hall(hall2)
      .withLinks(builder => {
        return builder
          .selfUrl(`/trainingSessions/${tsId2}`)
          .link('team', new HalLinkBuilder().href(team._links.self.href).build());
      })
      .template('deleteTrainingSession', {
        key: 'deleteTrainingSession',
        method: 'DELETE',
        properties: []
      })
      .build();

    // Add training sessions to the team
    const teamTrainingSessions = this.trainingSessions.get(team._links.self.href);
    if (teamTrainingSessions) {
      teamTrainingSessions.push(trainingSession1, trainingSession2);
    }
  }

  /**
   * Creates a role coach for a team
   * @param team The team to create a role coach for
   */
  private createRoleCoachForTeam(team: Team): void {
    // Create a coach for the role coach using CoachBuilder
    const coach = new CoachBuilder()
      .id(1)
      .name("John")
      .surname("Doe")
      .email("john.doe@example.com")
      .phone("123-456-7890")
      .withLinks(builder => {
        return builder.selfUrl(`/coaches/1`);
      })
      .build();

    // Create role coach using RoleCoachBuilder
    const rcId = this.nextId++;
    const roleCoach = new RoleCoachBuilder()
      .id(rcId)
      .coach(coach)
      .role(Role.MAIN)
      .team(team)
      .withLinks(builder => {
        return builder
          .selfUrl(`/roleCoaches/${rcId}`)
          .link('team', new HalLinkBuilder().href(team._links.self.href).build());
      })
      .template('deleteRoleCoach', {
        key: 'deleteRoleCoach',
        method: 'DELETE',
        properties: []
      })
      .build();

    // Add role coach to the team
    const teamRoleCoaches = this.roleCoaches.get(team._links.self.href);
    if (teamRoleCoaches) {
      teamRoleCoaches.push(roleCoach);
    }
  }

  /**
   * Retrieves a list of teams with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all teams
   * @returns Observable of paginated or all team resources
   */
  getTeams(paginationOption: PaginationOption = {
    size: 20,
    page: 0
  }): Observable<AllHalResources<Team> | PaginatedHalResource<Team>> {
    // Create a HAL-compliant response
    if (paginationOption === 'all') {
      const response: AllHalResources<Team> = {
        _links: {
          self: {href: '/teams'}
        },
        _embedded: {
          teams: this.teams
        }
      };
      return of(response);
    } else {
      const start = paginationOption.page * paginationOption.size;
      const end = start + paginationOption.size;
      const paginatedTeams = this.teams.slice(start, end);

      const response: PaginatedHalResource<Team> = {
        _links: {
          self: {href: '/teams'},
          first: {href: '/teams?page=0&size=' + paginationOption.size},
          last: {href: '/teams?page=' + Math.ceil(this.teams.length / paginationOption.size - 1) + '&size=' + paginationOption.size}
        },
        _embedded: {
          teams: paginatedTeams
        },
        page: {
          size: paginationOption.size,
          totalElements: this.teams.length,
          totalPages: Math.ceil(this.teams.length / paginationOption.size),
          number: paginationOption.page
        }
      };

      // Add next/prev links if applicable
      if (paginationOption.page > 0) {
        response._links.prev = {href: '/teams?page=' + (paginationOption.page - 1) + '&size=' + paginationOption.size};
      }

      if (paginationOption.page < Math.ceil(this.teams.length / paginationOption.size - 1)) {
        response._links.next = {href: '/teams?page=' + (paginationOption.page + 1) + '&size=' + paginationOption.size};
      }

      return of(response);
    }
  }

  /**
   * Creates a new team
   *
   * @param team - The team resource collection to add to
   * @param teamDtoCreateRequest - Data for the new team
   * @returns Observable of the created team
   */
  createTeam(team: HalResource, teamDtoCreateRequest: CreateTeamDTORequest): Observable<Team> {
    const id = this.nextId++;
    const newTeam = new TeamBuilder()
      .id(id)
      .gender(teamDtoCreateRequest.gender)
      .category(teamDtoCreateRequest.category)
      .teamNumber(teamDtoCreateRequest.teamNumber)
      .withLinks(builder => {
        return builder.selfUrl(`/teams/${id}`);
      })
      .template('updateTeam', {
        key: 'updateTeam',
        method: 'PUT',
        properties: []
      })
      .template('deleteTeam', {
        key: 'deleteTeam',
        method: 'DELETE',
        properties: []
      })
      .template('addTrainingSession', {
        key: 'addTrainingSession',
        method: 'POST',
        properties: []
      })
      .template('addRoleCoach', {
        key: 'addRoleCoach',
        method: 'POST',
        properties: []
      })
      .build();

    this.teams.push(newTeam);
    this.trainingSessions.set(newTeam._links.self.href, []);
    this.roleCoaches.set(newTeam._links.self.href, []);

    return of(newTeam);
  }

  /**
   * Updates an existing team
   *
   * @param team - The team to update
   * @param teamDtoUpdateRequest - Updated team data
   * @returns Observable of the updated team
   */
  updateTeam(team: Team, teamDtoUpdateRequest: CreateTeamDTORequest): Observable<Team> {
    const index = this.teams.findIndex(t => t._links.self.href === team._links.self.href);
    if (index === -1) {
      throw new Error(`Team with URI ${team._links.self.href} not found`);
    }

    // Create a new team with updated properties using TeamBuilder
    const updatedTeam = new TeamBuilder()
      .id(team.id || 0)
      .gender(teamDtoUpdateRequest.gender)
      .category(teamDtoUpdateRequest.category)
      .teamNumber(teamDtoUpdateRequest.teamNumber)
      .withLinks(builder => {
        return builder.selfUrl(team._links.self.href);
      })
      // Copy templates from the original team
      .properties(team._templates ? {_templates: team._templates} : {})
      .build();

    this.teams[index] = updatedTeam;
    return of(updatedTeam);
  }

  /**
   * Deletes a team
   *
   * @param team - The team to delete
   * @returns Observable of void
   */
  deleteTeam(team: Team): Observable<void> {
    const index = this.teams.findIndex(t => t._links.self.href === team._links.self.href);
    if (index === -1) {
      throw new Error(`Team with URI ${team._links.self.href} not found`);
    }

    this.teams.splice(index, 1);
    this.trainingSessions.delete(team._links.self.href);
    this.roleCoaches.delete(team._links.self.href);

    return of(void 0);
  }

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
  }> {
    return new Observable(observer => {
      this.createTeam(team, teamDtoCreateRequest).subscribe({
        next: (createdTeam) => {
          const trainingResults: TrainingSession[] = [];
          const roleCoachResults: RoleCoach[] = [];

          // Create training sessions
          for (const tsDTO of trainingSessionsDTORequest) {
            // Convert FormTrainingSessionDTO to AddTrainingSessionInTeamDTORequest
            const addTrainingSessionRequest = toAddTrainingSessionInTeamDTORequest(tsDTO);

            // Ensure required properties exist
            if (!addTrainingSessionRequest?.trainingSessionDTORequest?.timeSlot) {
              console.error('Missing timeSlot in training session request');
              continue;
            }

            if (!tsDTO?.hall) {
              console.error('Missing hall in training session DTO');
              continue;
            }

            const tsId = this.nextId++;

            // Create hall using HallBuilder
            const hall = new HallBuilder()
              .id(addTrainingSessionRequest.hallId)
              .name(tsDTO.hall.name || 'Unknown')
              .address(tsDTO.hall.address || {})
              .withLinks(builder => {
                return builder.selfUrl(`/halls/${addTrainingSessionRequest.hallId}`);
              })
              .build();

            // Create training session using TrainingSessionBuilder
            const ts = new TrainingSessionBuilder()
              .id(tsId)
              .timeSlot(addTrainingSessionRequest.trainingSessionDTORequest.timeSlot)
              .hall(hall)
              .withLinks(builder => {
                return builder
                  .selfUrl(`/trainingSessions/${tsId}`)
                  .link('team', new HalLinkBuilder().href(`/teams/${createdTeam.id || 0}`).build());
              })
              .template('deleteTrainingSession', {
                key: 'deleteTrainingSession',
                method: 'DELETE',
                properties: []
              })
              .build();
            trainingResults.push(ts);

            // Ensure the collection exists before pushing
            const teamTrainingSessions = this.trainingSessions.get(createdTeam._links.self.href);
            if (teamTrainingSessions) {
              teamTrainingSessions.push(ts);
            } else {
              console.error(`Training sessions collection not found for team: ${createdTeam._links.self.href}`);
            }
          }

          // Create role coaches
          for (const rcDTO of roleCoachesDTORequest) {
            // Convert FormRoleCoachDTO to AddRoleCoachInTeamDTORequest
            const addRoleCoachRequest = toAddRoleCoachInTeamDTORequest(rcDTO);

            // Ensure required properties exist
            if (!rcDTO?.coach) {
              console.error('Missing coach in role coach DTO');
              continue;
            }

            if (!addRoleCoachRequest?.role) {
              console.error('Missing role in role coach request');
              continue;
            }

            const rcId = this.nextId++;

            // Create role coach using RoleCoachBuilder
            const rc = new RoleCoachBuilder()
              .id(rcId)
              .coach(rcDTO.coach)
              .role(addRoleCoachRequest.role)
              .team(createdTeam)
              .withLinks(builder => {
                return builder
                  .selfUrl(`/roleCoaches/${rcId}`)
                  .link('team', new HalLinkBuilder().href(`/teams/${createdTeam.id || 0}`).build());
              })
              .template('deleteRoleCoach', {
                key: 'deleteRoleCoach',
                method: 'DELETE',
                properties: []
              })
              .build();
            roleCoachResults.push(rc);

            // Ensure the collection exists before pushing
            const teamRoleCoaches = this.roleCoaches.get(createdTeam._links.self.href);
            if (teamRoleCoaches) {
              teamRoleCoaches.push(rc);
            } else {
              console.error(`Role coaches collection not found for team: ${createdTeam._links.self.href}`);
            }
          }

          observer.next({
            team: createdTeam,
            trainingResults,
            roleCoachResults
          });
          observer.complete();
        },
        error: (error) => {
          observer.error(error);
        }
      });
    });
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
  }> {
    return new Observable(observer => {
      this.updateTeam(team, teamDtoUpdateRequest).subscribe({
        next: (updatedTeam) => {
          const trainingResults: TrainingSession[] = [];
          const roleCoachResults: RoleCoach[] = [];
          const deletedTrainingSessions: any[] = [];
          const deletedRoleCoaches: any[] = [];

          // Delete training sessions
          for (const ts of trainingSessionsToDelete) {
            // Skip if training session doesn't have required properties
            if (!ts?._links?.self?.href) {
              console.error('Training session missing self link');
              continue;
            }

            const teamTrainingSessions = this.trainingSessions.get(team._links.self.href);
            if (!teamTrainingSessions) {
              console.error(`Training sessions collection not found for team: ${team._links.self.href}`);
              continue;
            }

            const index = teamTrainingSessions.findIndex(t => t?._links?.self?.href === ts._links.self.href);
            if (index !== -1) {
              teamTrainingSessions.splice(index, 1);
              deletedTrainingSessions.push({});
            }
          }

          // Delete role coaches
          for (const rc of roleCoachesToDelete) {
            // Skip if role coach doesn't have required properties
            if (!rc?._links?.self?.href) {
              console.error('Role coach missing self link');
              continue;
            }

            const teamRoleCoaches = this.roleCoaches.get(team._links.self.href);
            if (!teamRoleCoaches) {
              console.error(`Role coaches collection not found for team: ${team._links.self.href}`);
              continue;
            }

            const index = teamRoleCoaches.findIndex(r => r?._links?.self?.href === rc._links.self.href);
            if (index !== -1) {
              teamRoleCoaches.splice(index, 1);
              deletedRoleCoaches.push({});
            }
          }

          // Add training sessions
          for (const tsDTO of trainingSessionsDTORequest) {
            // Convert FormTrainingSessionDTO to AddTrainingSessionInTeamDTORequest
            const addTrainingSessionRequest = toAddTrainingSessionInTeamDTORequest(tsDTO);

            // Ensure required properties exist
            if (!addTrainingSessionRequest?.trainingSessionDTORequest?.timeSlot) {
              console.error('Missing timeSlot in training session request');
              continue;
            }

            if (!tsDTO?.hall) {
              console.error('Missing hall in training session DTO');
              continue;
            }

            const tsId = this.nextId++;

            // Create hall using HallBuilder
            const hall = new HallBuilder()
              .id(addTrainingSessionRequest.hallId)
              .name(tsDTO.hall.name || 'Unknown')
              .address(tsDTO.hall.address || {})
              .withLinks(builder => {
                return builder.selfUrl(`/halls/${addTrainingSessionRequest.hallId}`);
              })
              .build();

            // Create training session using TrainingSessionBuilder
            const ts = new TrainingSessionBuilder()
              .id(tsId)
              .timeSlot(addTrainingSessionRequest.trainingSessionDTORequest.timeSlot)
              .hall(hall)
              .withLinks(builder => {
                return builder
                  .selfUrl(`/trainingSessions/${tsId}`)
                  .link('team', new HalLinkBuilder().href(`/teams/${updatedTeam.id || 0}`).build());
              })
              .template('deleteTrainingSession', {
                key: 'deleteTrainingSession',
                method: 'DELETE',
                properties: []
              })
              .build();
            trainingResults.push(ts);

            // Ensure the collection exists before pushing
            const teamTrainingSessions = this.trainingSessions.get(updatedTeam._links.self.href);
            if (teamTrainingSessions) {
              teamTrainingSessions.push(ts);
            } else {
              console.error(`Training sessions collection not found for team: ${updatedTeam._links.self.href}`);
            }
          }

          // Add role coaches
          for (const rcDTO of roleCoachesDTORequest) {
            // Convert FormRoleCoachDTO to AddRoleCoachInTeamDTORequest
            const addRoleCoachRequest = toAddRoleCoachInTeamDTORequest(rcDTO);

            // Ensure required properties exist
            if (!rcDTO?.coach) {
              console.error('Missing coach in role coach DTO');
              continue;
            }

            if (!addRoleCoachRequest?.role) {
              console.error('Missing role in role coach request');
              continue;
            }

            const rcId = this.nextId++;

            // Create role coach using RoleCoachBuilder
            const rc = new RoleCoachBuilder()
              .id(rcId)
              .coach(rcDTO.coach)
              .role(addRoleCoachRequest.role)
              .team(updatedTeam)
              .withLinks(builder => {
                return builder
                  .selfUrl(`/roleCoaches/${rcId}`)
                  .link('team', new HalLinkBuilder().href(`/teams/${updatedTeam.id || 0}`).build());
              })
              .template('deleteRoleCoach', {
                key: 'deleteRoleCoach',
                method: 'DELETE',
                properties: []
              })
              .build();
            roleCoachResults.push(rc);

            // Ensure the collection exists before pushing
            const teamRoleCoaches = this.roleCoaches.get(updatedTeam._links.self.href);
            if (teamRoleCoaches) {
              teamRoleCoaches.push(rc);
            } else {
              console.error(`Role coaches collection not found for team: ${updatedTeam._links.self.href}`);
            }
          }

          observer.next({
            team: updatedTeam,
            trainingResults,
            roleCoachResults,
            deletedTrainingSessions,
            deletedRoleCoaches
          });
          observer.complete();
        },
        error: (error) => {
          observer.error(error);
        }
      });
    });
  }

  /**
   * Retrieves training sessions associated with a team
   *
   * @param team - The team to get training sessions for
   * @returns Observable of training sessions array
   */
  getTrainingSessions(team: Team): Observable<TrainingSession[]> {
    const trainingSessions = this.trainingSessions.get(team._links.self.href) || [];
    return of(trainingSessions);
  }

  /**
   * Retrieves role coaches associated with a team
   *
   * @param team - The team to get role coaches for
   * @returns Observable of role coaches array
   */
  getRoleCoaches(team: Team): Observable<RoleCoach[]> {
    const roleCoaches = this.roleCoaches.get(team._links.self.href) || [];
    return of(roleCoaches);
  }

  /**
   * Retrieves a team by its URI
   *
   * @param uri - The URI of the team to retrieve
   * @returns Observable of the team
   */
  getTeam(uri: string): Observable<Team> {
    const team = this.teams.find(t => t._links.self.href === uri);
    if (!team) {
      throw new Error(`Team with URI ${uri} not found`);
    }
    return of(team);
  }
}
