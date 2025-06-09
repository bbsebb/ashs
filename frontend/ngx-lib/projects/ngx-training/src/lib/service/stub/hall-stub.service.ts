import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {AllHalResources, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Hall, HallBuilder} from '../../model/hall';
import {Address, AddressBuilder} from '../../model/address';
import {CreateHallDTORequest} from '../../dto/create-hall-d-t-o-request';
import {UpdateHallDTORequest} from '../../dto/update-hall-d-t-o-request';
import {IHallService} from '../i-hall.service';

/**
 * Stub implementation of HallService for testing purposes.
 * This implementation doesn't use halFormService but uses the Hall object type
 * that extends HalResource.
 */
@Injectable({
  providedIn: 'root',
})
export class HallStubService implements IHallService {
  // Mock data
  private halls: Hall[] = [];
  private nextId = 1;

  constructor() {
    // Initialize with some mock data
    this.init();
  }

  /**
   * Initializes the stub with two halls
   */
  init(): void {
    // Create first address
    const address1 = new AddressBuilder()
      .street("123 Main St")
      .city("New York")
      .postalCode("10001")
      .country("USA")
      .build();

    // Create second address
    const address2 = new AddressBuilder()
      .street("456 Park Ave")
      .city("Los Angeles")
      .postalCode("90001")
      .country("USA")
      .build();

    // Create first hall
    const id1 = this.nextId++;
    const hall1 = new HallBuilder()
      .id(id1)
      .name("Main Hall")
      .address(address1)
      .withLinks(builder => {
        return builder.selfUrl(`/halls/${id1}`);
      })
      .build();

    // Create second hall
    const id2 = this.nextId++;
    const hall2 = new HallBuilder()
      .id(id2)
      .name("Secondary Hall")
      .address(address2)
      .withLinks(builder => {
        return builder.selfUrl(`/halls/${id2}`);
      })
      .build();

    // Add halls to the array
    this.halls.push(hall1, hall2);
  }

  /**
   * Retrieves a list of halls with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all halls
   * @returns Observable of paginated or all hall resources
   */
  getHalls(paginationOption: PaginationOption = {
    size: 20,
    page: 0
  }): Observable<AllHalResources<Hall> | PaginatedHalResource<Hall>> {
    // Create a HAL-compliant response
    if (paginationOption === 'all') {
      const response: AllHalResources<Hall> = {
        _links: {
          self: {href: '/halls'}
        },
        _embedded: {
          halls: this.halls
        }
      };
      return of(response);
    } else {
      const start = paginationOption.page * paginationOption.size;
      const end = start + paginationOption.size;
      const paginatedHalls = this.halls.slice(start, end);

      const response: PaginatedHalResource<Hall> = {
        _links: {
          self: {href: '/halls'},
          first: {href: '/halls?page=0&size=' + paginationOption.size},
          last: {href: '/halls?page=' + Math.ceil(this.halls.length / paginationOption.size - 1) + '&size=' + paginationOption.size}
        },
        _embedded: {
          halls: paginatedHalls
        },
        page: {
          size: paginationOption.size,
          totalElements: this.halls.length,
          totalPages: Math.ceil(this.halls.length / paginationOption.size),
          number: paginationOption.page
        }
      };

      // Add next/prev links if applicable
      if (paginationOption.page > 0) {
        response._links.prev = {href: '/halls?page=' + (paginationOption.page - 1) + '&size=' + paginationOption.size};
      }

      if (paginationOption.page < Math.ceil(this.halls.length / paginationOption.size - 1)) {
        response._links.next = {href: '/halls?page=' + (paginationOption.page + 1) + '&size=' + paginationOption.size};
      }

      return of(response);
    }
  }

  /**
   * Creates a new hall
   *
   * @param hallResource - The hall resource collection to add to
   * @param createHallDTORequest - Data for the new hall
   * @returns Observable of the created hall
   */
  createHall(hallResource: AllHalResources<Hall> | PaginatedHalResource<Hall>, createHallDTORequest: CreateHallDTORequest): Observable<Hall> {
    const id = this.nextId++;
    const newHall: Hall = {
      id: id,
      name: createHallDTORequest.name,
      address: createHallDTORequest.address,
      _links: {
        self: {href: `/halls/${id}`}
      },
      _templates: {
        updateHall: {
          key: 'updateHall',
          method: 'PUT',
          properties: []
        },
        deleteHall: {
          key: 'deleteHall',
          method: 'DELETE',
          properties: []
        }
      }
    };

    this.halls.push(newHall);
    return of(newHall);
  }

  /**
   * Updates an existing hall
   *
   * @param hall - The hall to update
   * @param updateHallDTORequest - Updated hall data
   * @returns Observable of the updated hall
   */
  updateHall(hall: Hall, updateHallDTORequest: UpdateHallDTORequest): Observable<Hall> {
    const index = this.halls.findIndex(h => h._links.self.href === hall._links.self.href);
    if (index === -1) {
      throw new Error(`Hall with URI ${hall._links.self.href} not found`);
    }

    const updatedHall: Hall = {
      ...hall,
      name: updateHallDTORequest.name,
      address: updateHallDTORequest.address
    };

    this.halls[index] = updatedHall;
    return of(updatedHall);
  }

  /**
   * Deletes a hall
   *
   * @param hall - The hall to delete
   * @returns Observable of void
   */
  deleteHall(hall: Hall): Observable<void> {
    const index = this.halls.findIndex(h => h._links.self.href === hall._links.self.href);
    if (index === -1) {
      throw new Error(`Hall with URI ${hall._links.self.href} not found`);
    }

    this.halls.splice(index, 1);
    return of(void 0);
  }

  /**
   * Retrieves a hall by its URI
   *
   * @param uri - The URI of the hall to retrieve
   * @returns Observable of the hall
   */
  getHall(uri: string): Observable<Hall> {
    const hall = this.halls.find(h => h._links.self.href === uri);
    if (!hall) {
      throw new Error(`Hall with URI ${uri} not found`);
    }
    return of(hall);
  }
}
