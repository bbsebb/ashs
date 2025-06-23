import {rxResource} from '@angular/core/rxjs-interop';
import {computed, effect, inject, Injectable, Signal} from '@angular/core';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {addItemInEmbedded, deleteItemInEmbedded, setItemInEmbedded, unwrap} from 'ngx-hal-forms';

import {Hall} from '../model/hall';
import {CreateHallDTORequest} from '../dto/create-hall-d-t-o-request';
import {HALL_SERVICE} from '../service/i-hall.service';

@Injectable({
  providedIn: 'root'
})
export class HallsStore {
  private readonly hallService = inject(HALL_SERVICE);
  private readonly _hallsResource;


  constructor() {
    this._hallsResource = rxResource({
      loader: () => this.hallService.getHalls('all')
    })
    effect(() => {
      const error = this.hallsResource.error()
      if (error)
        console.error(
          "erreur dans le chargement de la ressource 'coaches' : ",
          error
        )
    });
  }

  get hallsResource() {
    return this._hallsResource;
  }

  get hallsHalResource() {
    return this.hallsResource.value;
  }

  get halls(): Signal<Hall[]> {
    return computed(() => {
      const hallsHalResource = this.hallsHalResource();
      if (hallsHalResource) {
        return unwrap<Hall[]>(hallsHalResource, 'halls')
      }
      return [];
    })
  }

  reloadHalls() {
    this.hallsResource.reload();
  }

  get hallsResourceIsLoading() {
    return this.hallsResource.isLoading;
  }

  get hallsResourceStatus() {
    return this.hallsResource.status;
  }

  get hallsResourceError() {
    return this.hallsResource.error;
  }

  getHallByUri(uri: string) {
    return computed(() => this.halls().find(hall => hall._links.self.href === uri))
  }

  createHall(hall: CreateHallDTORequest) {
    const hallsResource = this.hallsResource.value();
    if (!hallsResource) {
      throw new Error("Halls resource is undefined")
    }
    return this.hallService.createHall(hallsResource, hall).pipe(
      tap((res) => this.hallsResource.update((hallsResource) => addItemInEmbedded(hallsResource, 'halls', res))),
      tap(() => this.reloadHalls())
    );
  }

  updateHall(hall: Hall, updateHallDTORequest: CreateHallDTORequest) {
    return this.hallService.updateHall(hall, updateHallDTORequest).pipe(
      tap(() => this.hallsResource.update((hallsResource) => setItemInEmbedded(hallsResource, 'halls', hall))),
      tap(() => this.reloadHalls())
    )
  }

  deleteHall(hall: Hall): Observable<void> {
    return this.hallService.deleteHall(hall).pipe(
      tap(() => this.hallsResource.update((hallsResource) => deleteItemInEmbedded(hallsResource, 'halls', hall))),
      tap(() => this.reloadHalls())
    );
  }
}
