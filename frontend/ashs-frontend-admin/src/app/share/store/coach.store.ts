import {inject, Injectable, signal} from '@angular/core';
import {HalFormService} from '@app/share/service/hal-form.service';
import {rxResource} from '@angular/core/rxjs-interop';
import {CoachesStore} from '@app/share/store/coaches.store';
import {Coach} from '@app/share/model/coach';
import {of} from 'rxjs';
import {CreateCoachDTORequest} from '@app/share/service/dto/create-coach-d-t-o-request';
import {tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CoachStore {
  private readonly halFormService = inject(HalFormService);
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
        const coach = this.coachesStore.getCoachByUri(request);
        return coach ? of(coach) : this.halFormService.loadResource<Coach>(request);
      }
    })
  }

  set uri(uri: string | undefined) {
    this._uri.set(uri);
  }

  private get coachResource() {
    return this._coachResource;
  }

  getCoach() {
    return this.coachResource.value();
  }

  reloadCoach() {
    this.coachResource.reload();
  }

  coachResourceIsLoading() {
    return this.coachResource.isLoading();
  }

  getCoachResourceStatus() {
    return this.coachResource.status();
  }

  getCoachResourceError() {
    return this.coachResource.error();
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
