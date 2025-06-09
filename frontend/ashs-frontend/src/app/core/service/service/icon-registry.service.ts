import {inject, Injectable} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IconRegistryService {
  private readonly matIconRegistry = inject(MatIconRegistry);
  private readonly domSanitizer = inject(DomSanitizer);

  constructor() {
  }

  addInstagramIcon() {
    return of(this.matIconRegistry.addSvgIcon(
      'instagram',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/icons/instagram.svg')
    ));
  }
}
