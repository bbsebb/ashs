import {Component, computed, inject, input} from '@angular/core';
import {Feed} from '@app/share/model/feed';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {FormatDatePipe} from '@app/share/pipe/format-date.pipe';
import {CarouselComponent} from '@app/feature/instagram/carousel/carousel.component';
import {MediaComponent} from '@app/feature/instagram/media/media.component';
import {MatDialog} from '@angular/material/dialog';
import {FeedDialogComponent} from '@app/feature/instagram/feed-dialog/feed-dialog.component';
import {MatButton} from '@angular/material/button';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-feed',
  imports: [
    MatCard,
    MatCardContent,
    MatCardActions,
    FormatDatePipe,
    CarouselComponent,
    MediaComponent,
    MatButton
  ],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss'
})
export class FeedComponent {
  private readonly logger = inject(NGX_LOGGER);

  feedSignal = input.required<Feed>({
    alias: 'feed'
  });
  attachmentSignal = computed(() => this.feedSignal().attachments[0])
  private readonly dialog = inject(MatDialog);

  constructor() {
    this.logger.debug('Initialisation du composant Feed');
    this.logger.debug('Signaux initialisés: feedSignal, attachmentSignal');
  }

  openDialog() {
    const feed = this.feedSignal();
    this.logger.info('Ouverture du dialogue pour le feed');
    this.dialog.open(FeedDialogComponent, {data: {feed: feed}});
    this.logger.debug('Dialogue ouvert avec les données du feed');
  }
}
