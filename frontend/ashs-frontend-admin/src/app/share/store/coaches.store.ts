import {rxResource} from '@angular/core/rxjs-interop';
import {HalFormService} from '@app/share/service/hal-form.service';
import {inject, Injectable} from '@angular/core';
import {CoachService} from '@app/share/service/coach.service';
import {Coach} from '@app/share/model/coach';
import {CreateCoachDTORequest} from '@app/share/service/dto/create-coach-d-t-o-request';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CoachesStore {
  private readonly halFormService = inject(HalFormService);
  private readonly coachService = inject(CoachService);
  private readonly _coachesResource;


  constructor() {
    this._coachesResource = rxResource({
      loader: () => this.coachService.getCoachesHalResource('all')
    })
  }

  get coachesResource() {
    return this._coachesResource;
  }

  getCoachesHalResource() {
    return this.coachesResource.value();
  }

  getCoaches(): Coach[] {
    const coachesHalResource = this.getCoachesHalResource();
    if (coachesHalResource) {
      return this.halFormService.unwrap<Coach[]>(coachesHalResource, 'coaches')
    }
    return [];
  }

  reloadCoaches() {
    this.coachesResource.reload();
  }

  coachesResourceIsLoading() {
    return this.coachesResource.isLoading();
  }

  getCoachesResourceStatus() {
    return this.coachesResource.status();
  }

  getCoachesResourceError() {
    return this.coachesResource.error();
  }

  getCoachByUri(uri: string) {
    const coaches = this.getCoaches();
    return coaches.find(coach => coach._links.self.href === uri);
  }

  createCoach(coach: CreateCoachDTORequest) {
    const coachesResource = this.coachesResource.value();
    if (!coachesResource) {
      throw new Error("Coaches resource is undefined")
    }
    return this.coachService.createCoach(coachesResource, coach).pipe(
      tap((res) => this.coachesResource.update((coachesResource) => this.halFormService.addItemInEmbedded(coachesResource, 'coaches', res))),
      tap(() => this.reloadCoaches())
    );
  }

  updateCoach(coach: Coach, updateCoachDTORequest: CreateCoachDTORequest) {
    return this.coachService.updateCoach(coach, updateCoachDTORequest).pipe(
      tap(() => this.coachesResource.update((coachesResource) => this.halFormService.setItemInEmbedded(coachesResource, 'coaches', coach))),
      tap(() => this.reloadCoaches())
    )
  }

  deleteCoach(coach: Coach): Observable<void> {
    return this.coachService.deleteCoach(coach).pipe(
      tap(() => this.coachesResource.update((coachesResource) => this.halFormService.deleteItemInEmbedded(coachesResource, 'coaches', coach))),
      tap(() => this.reloadCoaches())
    );
  }
}
