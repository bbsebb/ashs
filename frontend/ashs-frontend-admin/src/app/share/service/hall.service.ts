import {inject, Injectable} from '@angular/core';
import {Hall} from '@app/share/model/hall';
import {HalFormService} from '@app/share/service/hal-form.service';
import {switchMap} from 'rxjs';
import {rxResource} from '@angular/core/rxjs-interop';
import {HalResource} from '@app/share/model/hal/hal';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class HallService {
  private readonly gatewayService = inject(HalFormService);
  private readonly _hallsResource = rxResource<Hall[], unknown>({
    loader: (() => this.gatewayService.root.pipe(
        switchMap(root => this.gatewayService.follow<HalResource<Hall>>(root, 'halls')),
        map(halResource => this.gatewayService.unwrap<Hall[]>(halResource, 'halls'))
      )
    )
  });

  constructor() {
  }


  get hallsResource() {
    return this._hallsResource;
  }

}
