import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NGX_LOGGER } from 'ngx-logger';

@Component({
  selector: 'app-page-not-found',
  standalone: true,
  imports: [RouterLink, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './page-not-found.component.html',
  styleUrl: './page-not-found.component.css'
})
export class PageNotFoundComponent {
  private readonly logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du composant Page Not Found');
  }
}
