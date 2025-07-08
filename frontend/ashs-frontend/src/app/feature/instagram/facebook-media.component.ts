import {Component, inject, Signal} from '@angular/core';
import {FeedStore} from '@app/share/service/feed.store';
import {Feed} from '@app/share/model/feed';
import {FeedComponent} from '@app/feature/instagram/feed/feed.component';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {GenericErrorComponent} from '@app/share/component/generic-error/generic-error.component';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-facebook-media',
  imports: [
    FeedComponent,
    MatProgressSpinner,
    MatPaginator,
    GenericErrorComponent
  ],
  templateUrl: './facebook-media.component.html',
  styleUrl: './facebook-media.component.scss'
})
export class FacebookMediaComponent {
  private readonly feedStore = inject(FeedStore);
  private readonly logger = inject(NGX_LOGGER);

  feedsSignal: Signal<Feed[]> = this.feedStore.getFeeds();
  paginationSignal = this.feedStore.getPagination();
  isLoadingSignal = this.feedStore.isLoading();
  errorSignal = this.feedStore.getError();

  constructor() {
    this.logger.debug('Initialisation du composant FacebookMedia');
    this.logger.debug('Signaux initialisés: feedsSignal, paginationSignal, isLoadingSignal, errorSignal');
  }

  next() {
    this.logger.info('Navigation vers la page suivante des feeds');
    this.feedStore.next();
    this.logger.debug('Demande de navigation vers la page suivante envoyée au store');
  }

  handlePageEvent($event: PageEvent) {
    this.logger.info('Événement de pagination reçu', {
      pageSize: $event.pageSize,
      pageIndex: $event.pageIndex
    });
    this.feedStore.updatePagination($event.pageSize, $event.pageIndex);
    this.logger.debug('Mise à jour de la pagination envoyée au store');
  }
}
