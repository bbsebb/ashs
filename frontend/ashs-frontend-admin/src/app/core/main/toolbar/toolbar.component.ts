import {Component, inject, input, output} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatIconButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';
import {KeycloakService} from '@app/share/service/keycloak.service';

@Component({
  selector: 'app-toolbar',
  imports: [
    MatToolbar,
    MatIcon,
    MatIconButton,
    MatButton,
    RouterLink
  ],
  templateUrl: './toolbar.component.html',
  standalone: true,
  styleUrl: './toolbar.component.css'
})
export class ToolbarComponent {
  isHandset = input.required();
  opening = output<void>();
  readonly keycloakService = inject(KeycloakService)

  constructor() {
  }

  login() {
    this.keycloakService.login();
  }

  logout() {
    this.keycloakService.logout();
  }
}
