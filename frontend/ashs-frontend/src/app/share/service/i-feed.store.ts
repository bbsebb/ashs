import {Pagination, PaginationOption} from 'ngx-hal-forms';
import {Signal} from '@angular/core';
import {Feed} from '@app/share/model/feed';

export interface IFeedStore {
  loadFeeds(pagination: PaginationOption | null): void;

  getFeeds(pagination: PaginationOption | null): Signal<Feed[]>;

  getError(): Signal<boolean>;

  isLoading(): Signal<boolean>;

  getStatus(): Signal<string>;

  hasPagination(): Signal<boolean>;

  getPagination(): Signal<Pagination | undefined>;

  hasNext(): void;

  next(): void;

  hasPrevious(): Signal<boolean>;

  previous(): void;

  hasFirst(): Signal<boolean>;

  first(): void;

  hasLast(): Signal<boolean>;

  last(): void;

  goTo(page: number): void;

  createFeed(feed: Feed): void;
}
