import {Component, inject} from '@angular/core';
import {NgOptimizedImage} from '@angular/common';
import {RouterLink} from '@angular/router';
import {MatIconAnchor} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-footer',
  imports: [
    NgOptimizedImage,
    RouterLink,
    MatIconAnchor,
    MatIcon,
    MatDivider
  ],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {
  private logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du composant Footer');
  }
}
