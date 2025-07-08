import {inject, Injectable} from '@angular/core';
import {Observable, of, switchMap} from 'rxjs';
import {AllHalResources, NgxHalFormsService, PaginatedHalResource, PaginationOption, unwrap} from 'ngx-hal-forms';
import {map} from 'rxjs/operators';
import {Coach} from '../model/coach';
import {CreateCoachDTORequest} from '../dto/create-coach-d-t-o-request';
import {ICoachService} from './i-coach.service';
import {NGX_LOGGER} from 'ngx-logger';

/**
 * Service for managing coach resources.
 * Provides CRUD operations for coaches using HAL-FORMS API.
 */
@Injectable({
  providedIn: 'root'
})
export class CoachService implements ICoachService {
  /**
   * Default pagination options for coach requests
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
    this.logger.debug('CoachService initialized');
  }

  /**
   * Retrieves a HAL resource of coaches with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all coaches
   * @returns Observable of paginated or all coach HAL resources
   */
  getCoachesHalResource(paginationOption: PaginationOption = CoachService.PAGINATION_OPTION_DEFAULT) {
    this.logger.debug('Getting coaches HAL resource', {paginationOption});

    return this.halFormService.root.pipe(
      switchMap((root) => {
        this.logger.debug('Following coaches link from root');
        return this.halFormService.follow<PaginatedHalResource<Coach>>(root, "coaches", this.halFormService.buildParamPage(paginationOption));
      }),
      switchMap((coachesRoot) => {
        if (paginationOption === 'all') {
          this.logger.debug('Getting all coaches');
          return this.halFormService.follow<AllHalResources<Coach>>(coachesRoot, "allCoaches");
        } else {
          this.logger.debug('Using paginated coaches', {
            page: paginationOption.page,
            size: paginationOption.size
          });
          return of(coachesRoot);
        }
      })
    );
  }

  /**
   * Retrieves a list of coaches with pagination support
   *
   * @param paginationOption - Pagination parameters or 'all' to retrieve all coaches
   * @returns Observable of coaches array
   */
  getCoaches(paginationOption: PaginationOption = CoachService.PAGINATION_OPTION_DEFAULT): Observable<Coach[]> {
    this.logger.debug('Getting coaches list', {paginationOption});

    return this.getCoachesHalResource(paginationOption).pipe(
      map(coaches => {
        const coachesList = unwrap<Coach[]>(coaches, 'coaches');
        this.logger.debug('Unwrapped coaches list', {count: coachesList.length});
        return coachesList;
      })
    )
  }

  /**
   * Creates a new coach
   *
   * @param coachResource - The coach resource collection to add to
   * @param createCoachDTORequest - Data for the new coach
   * @returns Observable of the created coach
   * @throws Error if the createCoach action is not available
   */
  createCoach(coachResource: AllHalResources<Coach> | PaginatedHalResource<Coach>, createCoachDTORequest: CreateCoachDTORequest) {
    this.logger.debug('Creating new coach', {
      firstName: createCoachDTORequest.name,
      lastName: createCoachDTORequest.surname,
      email: createCoachDTORequest.email
    });

    return this.halFormService.doAction<Coach>(coachResource, 'createCoach', createCoachDTORequest);
  }

  /**
   * Updates an existing coach
   *
   * @param coach - The coach to update
   * @param updateCoachDTORequest - Updated coach data
   * @returns Observable of the updated coach
   * @throws Error if the updateCoach action is not available
   */
  updateCoach(coach: Coach, updateCoachDTORequest: CreateCoachDTORequest) {
    this.logger.debug('Updating coach', {
      coachId: coach._links?.self?.href,
      firstName: updateCoachDTORequest.name,
      lastName: updateCoachDTORequest.surname,
      email: updateCoachDTORequest.email
    });

    return this.halFormService.doAction<Coach>(coach, 'updateCoach', updateCoachDTORequest);
  }

  /**
   * Deletes a coach
   *
   * @param coach - The coach to delete
   * @returns Observable of void
   * @throws Error if the deleteCoach action is not available
   */
  deleteCoach(coach: Coach) {
    this.logger.debug('Deleting coach', {
      coachId: coach._links?.self?.href,
      firstName: coach.name,
      lastName: coach.surname
    });

    return this.halFormService.doAction<void>(coach, 'deleteCoach');
  }

  /**
   * Retrieves a coach by its URI
   *
   * @param uri - The URI of the coach to retrieve
   * @returns Observable of the coach
   */
  getCoach(uri: string): Observable<Coach> {
    this.logger.debug('Getting coach by URI', {uri});

    return this.halFormService.loadResource<Coach>(uri);
  }
}
