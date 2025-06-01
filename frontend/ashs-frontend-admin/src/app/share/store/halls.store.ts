import {rxResource} from '@angular/core/rxjs-interop';
import {HalFormService} from '@app/share/service/hal-form.service';
import {inject, Injectable} from '@angular/core';
import {HallService} from '@app/share/service/hall.service';
import {Hall} from '@app/share/model/hall';
import {CreateHallDTORequest} from '@app/share/service/dto/create-hall-d-t-o-request';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HallsStore {
  private readonly halFormService = inject(HalFormService);
  private readonly hallService = inject(HallService);
  private readonly _hallsResource;


  constructor() {
    this._hallsResource = rxResource({
      loader: () => this.hallService.getHalls('all')
    })
  }

  get hallsResource() {
    return this._hallsResource;
  }

  getHallsHalResource() {
    return this.hallsResource.value();
  }

  getHalls(): Hall[] {
    const hallsHalResource = this.getHallsHalResource();
    if (hallsHalResource) {
      return this.halFormService.unwrap<Hall[]>(hallsHalResource, 'halls')
    }
    return [];
  }

  reloadHalls() {
    this.hallsResource.reload();
  }

  hallsResourceIsLoading() {
    return this.hallsResource.isLoading();
  }

  getHallsResourceStatus() {
    return this.hallsResource.status();
  }

  getHallsResourceError() {
    return this.hallsResource.error();
  }

  getHallByUri(uri: string) {
    const halls = this.getHalls();
    return halls.find(hall => hall._links.self.href === uri);
  }

  createHall(hall: CreateHallDTORequest) {
    const hallsResource = this.hallsResource.value();
    if (!hallsResource) {
      throw new Error("Halls resource is undefined")
    }
    return this.hallService.createHall(hallsResource, hall).pipe(
      tap((res) => this.hallsResource.update((hallsResource) => this.halFormService.addItemInEmbedded(hallsResource, 'halls', res))),
      tap(() => this.reloadHalls())
    );
  }

  updateHall(hall: Hall, updateHallDTORequest: CreateHallDTORequest) {
    return this.hallService.updateHall(hall, updateHallDTORequest).pipe(
      tap(() => this.hallsResource.update((hallsResource) => this.halFormService.setItemInEmbedded(hallsResource, 'halls', hall))),
      tap(() => this.reloadHalls())
    )
  }

  deleteHall(hall: Hall): Observable<void> {
    return this.hallService.deleteTeam(hall).pipe(
      tap(() => this.hallsResource.update((hallsResource) => this.halFormService.deleteItemInEmbedded(hallsResource, 'halls', hall))),
      tap(() => this.reloadHalls())
    );
  }
}
