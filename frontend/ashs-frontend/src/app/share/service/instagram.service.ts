import {inject, Injectable} from '@angular/core';
import {AllHalResources, NgxHalFormsService, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Feed} from '../model/feed';
import {switchMap} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InstagramService {

  private readonly halFormService = inject(NgxHalFormsService);

  constructor() {

  }


  getFeeds(paginationOption: PaginationOption) {
    return this.halFormService.root.pipe(
      switchMap(root => this.halFormService.follow<PaginatedHalResource<Feed> | AllHalResources<Feed>>(root, 'instagram', this.halFormService.buildParamPage(paginationOption))),
    )
  }


}
