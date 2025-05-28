import {inject, Injectable} from '@angular/core';
import {HalFormService} from '@app/share/service/hal-form.service';
import {rxResource} from '@angular/core/rxjs-interop';
import {switchMap} from 'rxjs';
import {HalResource} from '@app/share/model/hal/hal';
import {map} from 'rxjs/operators';
import {Coach} from '@app/share/model/coach';

@Injectable({
  providedIn: 'root'
})
export class CoachService {

  private readonly gatewayService = inject(HalFormService);
  private readonly _coachesResource = rxResource<Coach[], unknown>({
    loader: (() => this.gatewayService.root.pipe(
        switchMap(root => this.gatewayService.follow<HalResource<Coach>>(root, 'coaches')),
        map(halResource => this.gatewayService.unwrap<Coach[]>(halResource, 'coaches'))
      )
    )
  });

  constructor() {
  }


  get coachesResource() {
    return this._coachesResource;
  }
}
