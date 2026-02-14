import {inject, Injectable, signal} from '@angular/core';
import {rxResource} from '@angular/core/rxjs-interop';
import {of} from 'rxjs';
import {tap} from 'rxjs/operators';
import {HallsStore} from './halls.store';
import {HALL_SERVICE} from '../service/i-hall.service';
import {UpdateHallDTORequest} from '../dto/update-hall-d-t-o-request';

@Injectable()
export class HallStore {
  private readonly hallService = inject(HALL_SERVICE);
  private readonly hallsStore = inject(HallsStore);
  private readonly _uri = signal<string | undefined>(undefined);
  private readonly _hallResource;

  constructor() {
    this._hallResource = rxResource({
      request: () => {
        const uri = this._uri();
        if (uri) {
          return decodeURIComponent(uri);
        }
        return undefined
      },
      loader: ({request}) => {
        const hall = this.hallsStore.getHallByUri(request)();
        return hall ? of(hall) : this.hallService.getHall(request);
      }
    })
  }

  set uri(uri: string | undefined) {
    this._uri.set(uri);
  }

  private get hallResource() {
    return this._hallResource;
  }

  get hall() {
    return this.hallResource.value;
  }

  reloadHall() {
    this.hallResource.reload();
  }

  get hallResourceIsLoading() {
    return this.hallResource.isLoading;
  }

  get hallResourceStatus() {
    return this.hallResource.status;
  }

  get hallResourceError() {
    return this.hallResource.error;
  }

  updateHall(updateHallDTORequest: UpdateHallDTORequest) {
    const hall = this.hallResource.value();
    if (!hall) {
      throw new Error("Hall resource is undefined");
    }
    return this.hallsStore.updateHall(hall, updateHallDTORequest).pipe(
      tap(() => this.reloadHall()) //TODO A optimiser en modifiant directement le store
    )
  }
}
