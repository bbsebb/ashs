import {Component, inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogClose} from '@angular/material/dialog';
import {CarouselComponent} from '@app/feature/instagram/carousel/carousel.component';
import {FormatDatePipe} from '@app/share/pipe/format-date.pipe';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {MediaComponent} from '@app/feature/instagram/media/media.component';
import {MatButton} from '@angular/material/button';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-feed-dialog',
  imports: [
    CarouselComponent,
    FormatDatePipe,
    MatCard,
    MatCardActions,
    MatCardContent,
    MediaComponent,
    MatButton,
    MatDialogClose
  ],
  templateUrl: './feed-dialog.component.html',
  styleUrl: './feed-dialog.component.scss'
})
export class FeedDialogComponent {
  private readonly logger = inject(NGX_LOGGER);
  public readonly data = inject(MAT_DIALOG_DATA);
  feed = this.data.feed;
  attachment = this.feed.attachments[0];

  constructor() {
    this.logger.debug('Initialisation du composant FeedDialog');
    this.logger.debug('Données du feed reçues', { feedId: this.feed.id });
    this.logger.debug('Pièce jointe principale extraite', {
      attachmentType: this.attachment?.type,
      hasSubAttachments: this.attachment?.subAttachments?.length > 0
    });
  }
}
