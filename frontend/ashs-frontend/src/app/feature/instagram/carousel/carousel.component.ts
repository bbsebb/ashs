import {Component, computed, effect, inject, input, signal} from '@angular/core';
import {SubAttachment} from '@app/share/model/sub-attachment';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MediaComponent} from '@app/feature/instagram/media/media.component';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-carousel',
  imports: [
    MatIcon,
    MatIconButton,
    MediaComponent
  ],
  templateUrl: './carousel.component.html',
  styleUrl: './carousel.component.scss'
})
export class CarouselComponent {
  private readonly logger = inject(NGX_LOGGER);

  subAttachmentsSignal = input.required<SubAttachment[]>({alias: 'subAttachments'});
  currentIndex = signal(0);
  currentSubAttachment = computed(() => this.subAttachmentsSignal()[this.currentIndex()])

  constructor() {
    this.logger.debug('Initialisation du composant Carousel');

    effect(() => {
      const attachments = this.subAttachmentsSignal();
      this.logger.debug('Effet déclenché pour les pièces jointes', { count: attachments.length });
    });

    this.logger.debug('Signaux initialisés: subAttachmentsSignal, currentIndex, currentSubAttachment');
  }

  prevSlide() {
    const len = this.subAttachmentsSignal().length;
    const oldIndex = this.currentIndex();

    this.logger.debug('Navigation vers la diapositive précédente', { currentIndex: oldIndex, total: len });

    this.currentIndex.update((index) => {
      const newIndex = (index - 1 + len) % len;
      this.logger.debug('Index mis à jour', { oldIndex: index, newIndex });
      return newIndex;
    });
  }

  nextSlide() {
    const len = this.subAttachmentsSignal().length;
    const oldIndex = this.currentIndex();

    this.logger.debug('Navigation vers la diapositive suivante', { currentIndex: oldIndex, total: len });

    this.currentIndex.update((index) => {
      const newIndex = (index + 1) % len;
      this.logger.debug('Index mis à jour', { oldIndex: index, newIndex });
      return newIndex;
    });
  }
}
