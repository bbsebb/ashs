import {computed, inject, Injectable, signal, Signal, WritableSignal} from '@angular/core';

import {InstagramService} from '@app/share/service/instagram.service';
import {getPagination, hasPagination, PaginationOption, unwrap} from 'ngx-hal-forms';
import {Feed} from '@app/share/model/feed';
import {rxResource} from '@angular/core/rxjs-interop';

@Injectable({
  providedIn: 'root'
})
export class InstagramStore {
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 20,
    page: 0
  };
  private readonly instagramService = inject(InstagramService);
  _paginationOption: WritableSignal<PaginationOption> = signal(InstagramStore.PAGINATION_OPTION_DEFAULT);
  _feedsResource

  constructor() {
    this._feedsResource = this.loadFeeds();
  }

  loadFeeds() {
    return rxResource({
      request: () => this._paginationOption(),
      loader: ({request}) => this.instagramService.getFeeds(request)
    })
  }

  getFeeds(): Signal<Feed[]> {
    return computed(() => {
      const feedsResource = this._feedsResource.value();
      if (feedsResource) {
        return unwrap<Feed[]>(feedsResource, 'feedDTOResponseList')
      } else {
        return []
      }
    });
  }

  getError() {
    return this._feedsResource.error
  }

  isLoading() {
    return this._feedsResource.isLoading
  }

  getStatus() {
    return this._feedsResource.status
  }

  hasPagination(): Signal<boolean> {
    return computed(() => hasPagination(this._feedsResource.value()))
  }

  getPagination() {
    return computed(() => {
      return getPagination(this._feedsResource.value());
    });
  }

  next() {
    if (!this.hasPagination()) {
      throw new Error('No pagination')
    }
    this._paginationOption.update((pagination) => {
      if (pagination === 'all') {
        return pagination;
      }
      return {
        ...pagination,
        page: pagination.page + 1
      }
    })
  }
}
