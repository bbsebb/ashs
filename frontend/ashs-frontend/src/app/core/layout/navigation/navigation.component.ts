import {Component, inject} from '@angular/core';
import {MatListItem, MatNavList} from "@angular/material/list";
import {MatToolbar} from "@angular/material/toolbar";
import {RouterLink, RouterLinkActive} from '@angular/router';
import {NGX_LOGGER} from 'ngx-logger';

@Component({
  selector: 'app-navigation',
  imports: [
    MatListItem,
    MatNavList,
    MatToolbar,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent {
  private logger = inject(NGX_LOGGER);

  constructor() {
    this.logger.debug('Initialisation du composant Navigation');
  }
}
