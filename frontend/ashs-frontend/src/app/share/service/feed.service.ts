import {inject, Injectable} from '@angular/core';
import {AllHalResources, NgxHalFormsService, PaginatedHalResource, PaginationOption} from 'ngx-hal-forms';
import {Feed} from '../model/feed';
import {switchMap, tap} from 'rxjs';
import {IFeedService} from '@app/share/service/i-feed.service';
import {Attachment} from '../model/attachment';
import {NGX_LOGGER} from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class FeedService implements IFeedService {

  private readonly halFormService = inject(NgxHalFormsService);
  private readonly logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du service FeedService');
  }

  getAttachment(feed: Feed): Attachment {
    this.logger.debug('Récupération de la première pièce jointe pour le feed');
    return feed.attachments[0];
  }

  getFeeds(paginationOption: PaginationOption) {
    this.logger.info('Récupération des feeds avec pagination',
      JSON.stringify(paginationOption));

    return this.halFormService.root.pipe(
      tap(() => this.logger.debug('Root HAL récupéré, suivi du lien "facebook"')),
      switchMap(root => this.halFormService.follow<PaginatedHalResource<Feed> | AllHalResources<Feed>>(
        root,
        'facebook',
        this.halFormService.buildParamPage(paginationOption)
      )),
      tap(result => {
        if ('page' in result) {
          this.logger.info('Feeds récupérés avec succès',
            {totalElements: result.page.totalElements, totalPages: result.page.totalPages});
        } else {
          this.logger.info('Feeds récupérés avec succès', {count: result._embedded?.['feedDTOResponseList'].length || 0});
        }
      })
    );
  }
}
