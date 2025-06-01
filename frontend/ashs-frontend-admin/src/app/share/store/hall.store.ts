import {inject, Injectable, signal} from '@angular/core';
import {HalFormService} from '@app/share/service/hal-form.service';
import {rxResource} from '@angular/core/rxjs-interop';
import {HallsStore} from '@app/share/store/halls.store';
import {Hall} from '@app/share/model/hall';
import {of} from 'rxjs';
import {UpdateHallDTORequest} from '@app/share/service/dto/update-hall-d-t-o-request';
import {tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HallStore {
  private readonly halFormService = inject(HalFormService);
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
        const hall = this.hallsStore.getHallByUri(request);
        return hall ? of(hall) : this.halFormService.loadResource<Hall>(request);
      }
    })
  }

  set uri(uri: string | undefined) {
    this._uri.set(uri);
  }

  private get hallResource() {
    return this._hallResource;
  }

  getHall() {
    return this.hallResource.value();
  }

  reloadHall() {
    this.hallResource.reload();
  }

  hallResourceIsLoading() {
    return this.hallResource.isLoading();
  }

  getHallResourceStatus() {
    return this.hallResource.status();
  }

  getHallResourceError() {
    return this.hallResource.error();
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
