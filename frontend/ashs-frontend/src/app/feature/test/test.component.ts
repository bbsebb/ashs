import { Component, inject } from '@angular/core';
import { NGX_LOGGER } from 'ngx-logger';

@Component({
  selector: 'app-test',
  imports: [],
  templateUrl: './test.component.html',
  styleUrl: './test.component.scss'
})
export class TestComponent {
  private readonly logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du composant Test');
  }
}
