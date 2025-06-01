import {inject, Injectable} from '@angular/core';
import {HalFormService} from '@app/share/service/hal-form.service';
import {iif, Observable, switchMap} from 'rxjs';
import {AllHalResources, PaginatedHalResource} from '@app/share/model/hal/hal';
import {map} from 'rxjs/operators';
import {Coach} from '@app/share/model/coach';
import {PaginationOption} from '@app/share/service/pagination-option';
import {CreateCoachDTORequest} from '@app/share/service/dto/create-coach-d-t-o-request';

@Injectable({
  providedIn: 'root'
})
export class CoachService {
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 20,
    page: 0
  };
  private readonly halFormService = inject(HalFormService);

  constructor() {
  }

  getCoachesHalResource(paginationOption: PaginationOption = CoachService.PAGINATION_OPTION_DEFAULT) {
    return this.halFormService.root.pipe(
      switchMap((root) =>
        iif(
          () => paginationOption == 'all',
          this.halFormService.follow<AllHalResources<Coach>>(root, "allCoaches"),
          this.halFormService.follow<PaginatedHalResource<Coach>>(root, "coaches", this.halFormService.buildParamPage(paginationOption))
        )
      )
    );
  }

  getCoaches(paginationOption: PaginationOption = CoachService.PAGINATION_OPTION_DEFAULT): Observable<Coach[]> {
    return this.getCoachesHalResource(paginationOption).pipe(
      map(coaches => this.halFormService.unwrap<Coach[]>(coaches, 'coaches'))
    )
  }

  createCoach(coachResource: AllHalResources<Coach> | PaginatedHalResource<Coach>, createCoachDTORequest: CreateCoachDTORequest) {
    if (!this.halFormService.canAction(coachResource, 'createCoach')) {
      throw new Error("L'action createCoach n'est pas disponible sur l'objet " + createCoachDTORequest);
    }
    return this.halFormService.doAction<Coach>(coachResource, 'createCoach', createCoachDTORequest);
  }

  updateCoach(coach: Coach, updateCoachDTORequest: CreateCoachDTORequest) {
    if (!this.halFormService.canAction(coach, 'updateCoach')) {
      throw new Error("L'action updateCoach n'est pas disponible sur l'objet " + coach);
    }
    return this.halFormService.doAction<Coach>(coach, 'updateCoach', updateCoachDTORequest);
  }

  deleteCoach(coach: Coach) {
    if (!this.halFormService.canAction(coach, 'deleteCoach')) {
      throw new Error("L'action deleteCoach n'est pas disponible sur l'objet " + coach);
    }
    return this.halFormService.doAction<void>(coach, 'deleteCoach');
  }
}
