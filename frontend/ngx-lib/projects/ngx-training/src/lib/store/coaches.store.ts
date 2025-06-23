import {rxResource} from '@angular/core/rxjs-interop';
import {computed, effect, inject, Injectable, Signal} from '@angular/core';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {addItemInEmbedded, deleteItemInEmbedded, setItemInEmbedded, unwrap} from 'ngx-hal-forms';
import {Coach} from '../model/coach';
import {CreateCoachDTORequest} from '../dto/create-coach-d-t-o-request';
import {COACH_SERVICE} from '../service/i-coach.service';

@Injectable({
  providedIn: 'root'
})
export class CoachesStore {

  private readonly coachService = inject(COACH_SERVICE);
  private readonly _coachesResource;


  constructor() {
    this._coachesResource = rxResource({
      loader: () => this.coachService.getCoachesHalResource('all')
    })
    effect(() => {
      const error = this.coachesResource.error()
      if (error)
        console.error(
          "erreur dans le chargement de la ressource 'coaches' : ",
          error
        )
    });
  }

  get coachesResource() {
    return this._coachesResource;
  }

  get coachesHalResource() {
    return this.coachesResource.value;
  }

  get coaches(): Signal<Coach[]> {
    return computed(() => {
      const coachesHalResource = this.coachesHalResource();
      if (coachesHalResource) {
        return unwrap<Coach[]>(coachesHalResource, 'coaches')
      }
      return [];
    })
  }

  reloadCoaches() {
    this.coachesResource.reload();
  }

  get coachesResourceIsLoading() {
    return this.coachesResource.isLoading;
  }

  get coachesResourceStatus() {
    return this.coachesResource.status;
  }

  get coachesResourceError() {
    return this.coachesResource.error;
  }

  getCoachByUri(uri: string) {
    return computed(() => this.coaches().find(coach => coach._links.self.href === uri))
  }

  createCoach(coach: CreateCoachDTORequest) {
    const coachesResource = this.coachesResource.value();
    if (!coachesResource) {
      throw new Error("Coaches resource is undefined")
    }
    return this.coachService.createCoach(coachesResource, coach).pipe(
      tap((res) => this.coachesResource.update((coachesResource) => addItemInEmbedded(coachesResource, 'coaches', res))),
      tap(() => this.reloadCoaches())
    );
  }

  updateCoach(coach: Coach, updateCoachDTORequest: CreateCoachDTORequest) {
    return this.coachService.updateCoach(coach, updateCoachDTORequest).pipe(
      tap(() => this.coachesResource.update((coachesResource) => setItemInEmbedded(coachesResource, 'coaches', coach))),
      tap(() => this.reloadCoaches())
    )
  }

  deleteCoach(coach: Coach): Observable<void> {
    return this.coachService.deleteCoach(coach).pipe(
      tap(() => this.coachesResource.update((coachesResource) => deleteItemInEmbedded(coachesResource, 'coaches', coach))),
      tap(() => this.reloadCoaches())
    );
  }
}
