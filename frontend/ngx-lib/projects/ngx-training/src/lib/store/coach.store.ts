import {inject, Injectable, signal} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {of} from 'rxjs';
import {tap} from 'rxjs/operators';
import {CoachesStore} from './coaches.store';
import {CreateCoachDTORequest} from '../dto/create-coach-d-t-o-request';
import {COACH_SERVICE} from '../service/i-coach.service';

@Injectable({
  providedIn: 'root'
})
export class CoachStore {
  private readonly coachService = inject(COACH_SERVICE);
  private readonly coachesStore = inject(CoachesStore);
  private readonly _uri = signal<string | undefined>(undefined);
  private readonly _coachResource;

  constructor() {
    this._coachResource = rxResource({
      request: () => {
        const uri = this._uri();
        if (uri) {
          return decodeURIComponent(uri);
        }
        return undefined
      },
      loader: ({request}) => {
        const coach = this.coachesStore.getCoachByUri(request)();
        return coach ? of(coach) : this.coachService.getCoach(request);
      }
    })
  }

  set uri(uri: string | undefined) {
    this._uri.set(uri);
  }

  private get coachResource() {
    return this._coachResource;
  }

  get coach() {
    return this.coachResource.value;
  }

  reloadCoach() {
    this.coachResource.reload();
  }

  get coachResourceIsLoading() {
    return this.coachResource.isLoading;
  }

  get coachResourceStatus() {
    return this.coachResource.status;
  }

  get coachResourceError() {
    return this.coachResource.error;
  }

  updateCoach(updateCoachDTORequest: CreateCoachDTORequest) {
    const coach = this.coachResource.value();
    if (!coach) {
      throw new Error("Coach resource is undefined");
    }
    return this.coachesStore.updateCoach(coach, updateCoachDTORequest).pipe(
      tap(() => this.reloadCoach()) //TODO A optimiser en modifiant directement le store
    )
  }
}
