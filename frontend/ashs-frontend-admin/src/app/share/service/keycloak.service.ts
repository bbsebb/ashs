import {Injectable} from '@angular/core';
import Keycloak, {KeycloakInitOptions, KeycloakLoginOptions} from 'keycloak-js';
import {environment} from '@environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private readonly keycloak: Keycloak;

  constructor() {
    this.keycloak = new Keycloak({
      url: `${environment.keycloakUrl}`,
      realm: 'ashs',
      clientId: 'angular-frontend-admin'
    });
  }

  /**
   * Initialise Keycloak lors du démarrage de l’application.
   * Cette méthode doit être appelée via `provideAppInitializer()`.
   */
  async init(options?: KeycloakInitOptions): Promise<boolean> {
    const defaultOptions: KeycloakInitOptions = {
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      checkLoginIframe: false,
    };

    try {
      return await this.keycloak
        .init({...defaultOptions, ...options}).then(authenticated => {
          if (!authenticated) {
            this.login();
          }
          return authenticated
        });
    } catch (err) {
      console.error('[Keycloak] Init failed', err);
      return false;
    }
  }

  login(options?: KeycloakLoginOptions): void {
    void this.keycloak.login(options);
  }

  logout(redirectUri?: string): void {
    void this.keycloak.logout({redirectUri});
  }

  isAuthenticated(): boolean {
    return this.keycloak?.authenticated ?? false;
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }

  getUsername(): string | undefined {
    return this.keycloak?.tokenParsed?.['preferred_username'];
  }

  getRoles(): string[] {
    return this.keycloak?.realmAccess?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  getKeycloakInstance(): Keycloak {
    return this.keycloak;
  }
}
