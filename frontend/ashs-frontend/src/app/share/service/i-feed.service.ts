import {AllHalResources, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Feed} from '@app/share/model/feed';
import {Observable} from 'rxjs';
import {Attachment} from '@app/share/model/attachment';

export interface IFeedService {


  getFeeds(pagination: PaginationOption | null): Observable<AllHalResources<Feed> | PaginatedHalResource<Feed>>;


  getAttachment(feed: Feed): Attachment;

}
