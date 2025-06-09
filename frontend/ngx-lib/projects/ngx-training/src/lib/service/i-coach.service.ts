import {Observable} from 'rxjs';
import {AllHalResources, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Coach} from '../model/coach';
import {CreateCoachDTORequest} from '../dto/create-coach-d-t-o-request';
import {InjectionToken} from '@angular/core';

/**
 * Interface for coach service operations.
 * Defines contract for CRUD operations on coaches using HAL-FORMS API.
 */
export interface ICoachService {
  /**
   * Retrieves a HAL resource of coaches with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all coaches
   * @returns Observable of paginated or all coach HAL resources
   */
  getCoachesHalResource(paginationOption?: PaginationOption): Observable<AllHalResources<Coach> | PaginatedHalResource<Coach>>;

  /**
   * Retrieves a list of coaches with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all coaches
   * @returns Observable of coaches array
   */
  getCoaches(paginationOption?: PaginationOption): Observable<Coach[]>;

  /**
   * Creates a new coach
   *
   * @param coachResource - The coach resource collection to add to
   * @param createCoachDTORequest - Data for the new coach
   * @returns Observable of the created coach
   * @throws Error if the createCoach action is not available
   */
  createCoach(coachResource: AllHalResources<Coach> | PaginatedHalResource<Coach>, createCoachDTORequest: CreateCoachDTORequest): Observable<Coach>;

  /**
   * Updates an existing coach
   *
   * @param coach - The coach to update
   * @param updateCoachDTORequest - Updated coach data
   * @returns Observable of the updated coach
   * @throws Error if the updateCoach action is not available
   */
  updateCoach(coach: Coach, updateCoachDTORequest: CreateCoachDTORequest): Observable<Coach>;

  /**
   * Deletes a coach
   *
   * @param coach - The coach to delete
   * @returns Observable of void
   * @throws Error if the deleteCoach action is not available
   */
  deleteCoach(coach: Coach): Observable<void>;

  /**
   * Retrieves a coach by its URI
   *
   * @param uri - The URI of the coach to retrieve
   * @returns Observable of the coach
   */
  getCoach(uri: string): Observable<Coach>;
}

export const COACH_SERVICE = new InjectionToken<ICoachService>('CoachService');
