import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {AllHalResources, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Coach, CoachBuilder} from '../../model/coach';
import {CreateCoachDTORequest} from '../../dto/create-coach-d-t-o-request';
import {ICoachService} from '../i-coach.service';

/**
 * Stub implementation of CoachService for testing purposes.
 * This implementation doesn't use halFormService but uses the Coach object type
 * that extends HalResource.
 */
@Injectable({
  providedIn: 'root',
})
export class CoachStubService implements ICoachService {
  // Mock data
  private coaches: Coach[] = [];
  private nextId = 1;

  constructor() {
    // Initialize with some mock data
    this.init();
  }

  /**
   * Initializes the stub with two coaches
   */
  init(): void {
    // Create first coach
    const id1 = this.nextId++;
    const coach1 = new CoachBuilder()
      .id(id1)
      .name("John")
      .surname("Doe")
      .email("john.doe@example.com")
      .phone("123-456-7890")
      .withLinks(builder => {
        return builder.selfUrl(`/coaches/${id1}`);
      })
      .build();

    // Create second coach
    const id2 = this.nextId++;
    const coach2 = new CoachBuilder()
      .id(id2)
      .name("Jane")
      .surname("Smith")
      .email("jane.smith@example.com")
      .phone("987-654-3210")
      .withLinks(builder => {
        return builder.selfUrl(`/coaches/${id2}`);
      })
      .build();

    // Add coaches to the array
    this.coaches.push(coach1, coach2);
  }

  /**
   * Retrieves a HAL resource of coaches with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all coaches
   * @returns Observable of paginated or all coach HAL resources
   */
  getCoachesHalResource(paginationOption: PaginationOption = {
    size: 20,
    page: 0
  }): Observable<AllHalResources<Coach> | PaginatedHalResource<Coach>> {
    // Create a HAL-compliant response
    if (paginationOption === 'all') {
      const response: AllHalResources<Coach> = {
        _links: {
          self: {href: '/coaches'}
        },
        _embedded: {
          coaches: this.coaches
        }
      };
      return of(response);
    } else {
      const start = paginationOption.page * paginationOption.size;
      const end = start + paginationOption.size;
      const paginatedCoaches = this.coaches.slice(start, end);

      const response: PaginatedHalResource<Coach> = {
        _links: {
          self: {href: '/coaches'},
          first: {href: '/coaches?page=0&size=' + paginationOption.size},
          last: {href: '/coaches?page=' + Math.ceil(this.coaches.length / paginationOption.size - 1) + '&size=' + paginationOption.size}
        },
        _embedded: {
          coaches: paginatedCoaches
        },
        page: {
          size: paginationOption.size,
          totalElements: this.coaches.length,
          totalPages: Math.ceil(this.coaches.length / paginationOption.size),
          number: paginationOption.page
        }
      };

      // Add next/prev links if applicable
      if (paginationOption.page > 0) {
        response._links.prev = {href: '/coaches?page=' + (paginationOption.page - 1) + '&size=' + paginationOption.size};
      }

      if (paginationOption.page < Math.ceil(this.coaches.length / paginationOption.size - 1)) {
        response._links.next = {href: '/coaches?page=' + (paginationOption.page + 1) + '&size=' + paginationOption.size};
      }

      return of(response);
    }
  }

  /**
   * Retrieves a list of coaches with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all coaches
   * @returns Observable of coaches array
   */
  getCoaches(paginationOption: PaginationOption = {size: 20, page: 0}): Observable<Coach[]> {
    if (paginationOption === 'all') {
      return of(this.coaches);
    } else {
      const start = paginationOption.page * paginationOption.size;
      const end = start + paginationOption.size;
      return of(this.coaches.slice(start, end));
    }
  }

  /**
   * Creates a new coach
   *
   * @param coachResource - The coach resource collection to add to
   * @param createCoachDTORequest - Data for the new coach
   * @returns Observable of the created coach
   */
  createCoach(coachResource: AllHalResources<Coach> | PaginatedHalResource<Coach>, createCoachDTORequest: CreateCoachDTORequest): Observable<Coach> {
    const id = this.nextId++;
    const newCoach: Coach = {
      id: id,
      name: createCoachDTORequest.name,
      surname: createCoachDTORequest.surname,
      email: createCoachDTORequest.email,
      phone: createCoachDTORequest.phone,
      _links: {
        self: {href: `/coaches/${id}`}
      },
      _templates: {
        updateCoach: {
          key: 'updateCoach',
          method: 'PUT',
          properties: []
        },
        deleteCoach: {
          key: 'deleteCoach',
          method: 'DELETE',
          properties: []
        }
      }
    };

    this.coaches.push(newCoach);
    return of(newCoach);
  }

  /**
   * Updates an existing coach
   *
   * @param coach - The coach to update
   * @param updateCoachDTORequest - Updated coach data
   * @returns Observable of the updated coach
   */
  updateCoach(coach: Coach, updateCoachDTORequest: CreateCoachDTORequest): Observable<Coach> {
    const index = this.coaches.findIndex(c => c._links.self.href === coach._links.self.href);
    if (index === -1) {
      throw new Error(`Coach with URI ${coach._links.self.href} not found`);
    }

    const updatedCoach: Coach = {
      ...coach,
      name: updateCoachDTORequest.name,
      surname: updateCoachDTORequest.surname,
      email: updateCoachDTORequest.email,
      phone: updateCoachDTORequest.phone
    };

    this.coaches[index] = updatedCoach;
    return of(updatedCoach);
  }

  /**
   * Deletes a coach
   *
   * @param coach - The coach to delete
   * @returns Observable of void
   */
  deleteCoach(coach: Coach): Observable<void> {
    const index = this.coaches.findIndex(c => c._links.self.href === coach._links.self.href);
    if (index === -1) {
      throw new Error(`Coach with URI ${coach._links.self.href} not found`);
    }

    this.coaches.splice(index, 1);
    return of(void 0);
  }

  /**
   * Retrieves a coach by its URI
   *
   * @param uri - The URI of the coach to retrieve
   * @returns Observable of the coach
   */
  getCoach(uri: string): Observable<Coach> {
    const coach = this.coaches.find(c => c._links.self.href === uri);
    if (!coach) {
      throw new Error(`Coach with URI ${uri} not found`);
    }
    return of(coach);
  }
}
