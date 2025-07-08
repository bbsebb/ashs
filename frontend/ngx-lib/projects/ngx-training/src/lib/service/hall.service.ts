import {inject, Injectable} from '@angular/core';
import {iif, Observable, of, switchMap} from 'rxjs';
import {AllHalResources, NgxHalFormsService, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {UpdateHallDTORequest} from '../dto/update-hall-d-t-o-request';
import {Hall} from '../model/hall';
import {CreateHallDTORequest} from '../dto/create-hall-d-t-o-request';
import {IHallService} from './i-hall.service';
import {NGX_LOGGER, NgxLoggerService} from 'ngx-logger';

/**
 * Service for managing hall resources.
 * Provides CRUD operations for halls using HAL-FORMS API.
 */
@Injectable({
  providedIn: 'root'
})
export class HallService implements IHallService {
  /**
   * Default pagination options for hall requests
   */
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 20,
    page: 0
  };

  /**
   * HAL Forms service for API communication
   */
  private readonly halFormService = inject(NgxHalFormsService);

  /**
   * Logger service
   */
  private readonly logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('HallService initialized');
  }

  /**
   * Retrieves a list of halls with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all halls
   * @returns Observable of paginated or all hall resources
   */
  getHalls(paginationOption: PaginationOption = HallService.PAGINATION_OPTION_DEFAULT) {
    this.logger.debug('Getting halls', { paginationOption });

    return this.halFormService.root.pipe(
      switchMap((root) => {
        this.logger.debug('Following halls link from root');
        return this.halFormService.follow<PaginatedHalResource<Hall>>(root, "halls", this.halFormService.buildParamPage(paginationOption));
      }),
      switchMap((hallsRoot) => {
        if (paginationOption === 'all') {
          this.logger.debug('Getting all halls');
          return this.halFormService.follow<AllHalResources<Hall>>(hallsRoot, "allHalls");
        } else {
          this.logger.debug('Using paginated halls', {
            page: paginationOption.page,
            size: paginationOption.size
          });
          return of(hallsRoot);
        }
      })
    );
  }

  /**
   * Creates a new hall
   *
   * @param hallResource - The hall resource collection to add to
   * @param createHallDTORequest - Data for the new hall
   * @returns Observable of the created hall
   * @throws Error if the createHall action is not available
   */
  createHall(hallResource: AllHalResources<Hall> | PaginatedHalResource<Hall>, createHallDTORequest: CreateHallDTORequest) {
    this.logger.debug('Creating new hall', {
      name: createHallDTORequest.name,
      address: createHallDTORequest.address
    });

    return this.halFormService.doAction<Hall>(hallResource, 'createHall', createHallDTORequest);
  }

  /**
   * Updates an existing hall
   *
   * @param hall - The hall to update
   * @param updateHallDTORequest - Updated hall data
   * @returns Observable of the updated hall
   * @throws Error if the updateHall action is not available
   */
  updateHall(hall: Hall, updateHallDTORequest: UpdateHallDTORequest) {
    this.logger.debug('Updating hall', {
      hallId: hall._links?.self?.href,
      name: updateHallDTORequest.name,
      address: updateHallDTORequest.address
    });

    return this.halFormService.doAction<Hall>(hall, 'updateHall', updateHallDTORequest);
  }

  /**
   * Deletes a hall
   *
   * @param hall - The hall to delete
   * @returns Observable of void
   * @throws Error if the deleteHall action is not available
   */
  deleteHall(hall: Hall) {
    this.logger.debug('Deleting hall', {
      hallId: hall._links?.self?.href,
      name: hall.name
    });

    if (!this.halFormService.canAction(hall, 'deleteHall')) {
      this.logger.error("Cannot delete hall - action not available", {
        hallId: hall._links?.self?.href
      });
      throw new Error("L'action deleteHall n'est pas disponible sur l'objet " + hall);
    }

    return this.halFormService.doAction<void>(hall, 'deleteHall');
  }

  /**
   * Retrieves a hall by its URI
   *
   * @param uri - The URI of the hall to retrieve
   * @returns Observable of the hall
   */
  getHall(uri: string): Observable<Hall> {
    this.logger.debug('Getting hall by URI', { uri });

    return this.halFormService.loadResource<Hall>(uri);
  }
}
