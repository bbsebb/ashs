import { Component, inject } from '@angular/core';
import { NGX_LOGGER } from 'ngx-logger';

@Component({
  selector: 'app-mentions-legales',
  standalone: true,
  imports: [],
  templateUrl: './mentions-legales.component.html',
  styleUrl: './mentions-legales.component.scss'
})
export class MentionsLegalesComponent {
  private readonly logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du composant MentionsLegales');
  }
}
