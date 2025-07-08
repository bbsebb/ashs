import {Component, effect, inject, input} from '@angular/core';
import {Media} from '@app/share/model/media';
import {NgOptimizedImage} from '@angular/common';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-media',
  imports: [
    NgOptimizedImage
  ],
  templateUrl: './media.component.html',
  styleUrl: './media.component.scss'
})
export class MediaComponent {
  private readonly logger = inject(NGX_LOGGER);

  mediaSignal = input.required<Media>({alias: 'media'});
  typeSignal = input.required<string>({alias: 'type'});

  constructor() {
    this.logger.debug('Initialisation du composant Media');

    effect(() => {
      const media = this.mediaSignal();
      const type = this.typeSignal();
      this.logger.debug('Effet déclenché pour les signaux media et type', {
        mediaType: media,
        mediaUrl: media.source?.substring(0, 50) + '...',
        type: type
      });
    });

    this.logger.debug('Signaux initialisés: mediaSignal, typeSignal');
  }
}
