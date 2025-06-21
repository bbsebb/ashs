import {inject, Injectable, signal} from '@angular/core';
import {KeycloakService} from './keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly keycloakService: KeycloakService = inject(KeycloakService);

  username = signal<string | undefined>(undefined);

  constructor() {
    this.init();
  }

  private init() {


  }

  login() {
    this.keycloakService.login().subscribe()
  }

  logout() {
    this.keycloakService.logout().subscribe()
  }
}
