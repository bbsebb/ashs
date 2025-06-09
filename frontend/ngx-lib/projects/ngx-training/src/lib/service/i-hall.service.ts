import {Observable} from 'rxjs';
import {AllHalResources, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Hall} from '../model/hall';
import {CreateHallDTORequest} from '../dto/create-hall-d-t-o-request';
import {UpdateHallDTORequest} from '../dto/update-hall-d-t-o-request';
import {InjectionToken} from '@angular/core';

/**
 * Interface for hall service operations.
 * Defines contract for CRUD operations on halls using HAL-FORMS API.
 */
export interface IHallService {
  /**
   * Retrieves a list of halls with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all halls
   * @returns Observable of paginated or all hall resources
   */
  getHalls(paginationOption?: PaginationOption): Observable<AllHalResources<Hall> | PaginatedHalResource<Hall>>;

  /**
   * Creates a new hall
   *
   * @param hallResource - The hall resource collection to add to
   * @param createHallDTORequest - Data for the new hall
   * @returns Observable of the created hall
   * @throws Error if the createHall action is not available
   */
  createHall(hallResource: AllHalResources<Hall> | PaginatedHalResource<Hall>, createHallDTORequest: CreateHallDTORequest): Observable<Hall>;

  /**
   * Updates an existing hall
   *
   * @param hall - The hall to update
   * @param updateHallDTORequest - Updated hall data
   * @returns Observable of the updated hall
   * @throws Error if the updateHall action is not available
   */
  updateHall(hall: Hall, updateHallDTORequest: UpdateHallDTORequest): Observable<Hall>;

  /**
   * Deletes a hall
   *
   * @param hall - The hall to delete
   * @returns Observable of void
   * @throws Error if the deleteHall action is not available
   */
  deleteHall(hall: Hall): Observable<void>;

  /**
   * Retrieves a hall by its URI
   *
   * @param uri - The URI of the hall to retrieve
   * @returns Observable of the hall
   */
  getHall(uri: string): Observable<Hall>;
}

export const HALL_SERVICE = new InjectionToken<IHallService>('HallService');
