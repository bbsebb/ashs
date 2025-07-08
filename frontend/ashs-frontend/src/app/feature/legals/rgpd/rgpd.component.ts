import { Component, inject } from '@angular/core';
import { NGX_LOGGER } from 'ngx-logger';

@Component({
  selector: 'app-rgpd',
  standalone: true,
  imports: [],
  templateUrl: './rgpd.component.html',
  styleUrl: './rgpd.component.scss'
})
export class RgpdComponent {
  private readonly logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du composant RGPD');
  }
}
