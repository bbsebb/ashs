import {inject, Injectable} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {of, tap} from 'rxjs';
import {NGX_LOGGER} from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class IconRegistryService {
  private readonly matIconRegistry = inject(MatIconRegistry);
  private readonly domSanitizer = inject(DomSanitizer);
  private readonly logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du service IconRegistryService');
  }

  addInstagramIcon() {
    this.logger.debug('Ajout de l\'icône Instagram au registre d\'icônes');
    return of(this.matIconRegistry.addSvgIcon(
      'instagram',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/icons/instagram.svg')
    )).pipe(
      tap(() => this.logger.debug('Icône Instagram ajoutée avec succès'))
    );
  }
}
