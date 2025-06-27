import {AllHalResources, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Feed} from '@app/share/model/feed';
import {Observable} from 'rxjs';

export interface IInstagramService {


  getFeeds(pagination: PaginationOption | null): Observable<AllHalResources<Feed> | PaginatedHalResource<Feed>>;

  createFeed(feed: Feed): void;


}
