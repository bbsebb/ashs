import {effect, inject, Injectable, signal} from '@angular/core';
import Keycloak, {KeycloakProfile} from 'keycloak-js';
import {BehaviorSubject, from, Observable} from 'rxjs';
import {KEYCLOAK_EVENT_SIGNAL, KeycloakEventType, ReadyArgs, typeEventArgs} from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private readonly keycloak = inject(Keycloak);
  private readonly keycloakEventSignal = inject(KEYCLOAK_EVENT_SIGNAL);
  private userProfile$ = new BehaviorSubject<KeycloakProfile | null>(null);

  // Signal pour suivre l'état d'authentification
  public readonly isAuthenticated = signal<boolean>(false);

  constructor() {
    this.setupKeycloakEvents();
    void this.loadUserProfile();
  }

  /**
   * Configuration des événements Keycloak
   */
  private setupKeycloakEvents(): void {
    effect(() => {
      const keycloakEvent = this.keycloakEventSignal();

      switch (keycloakEvent.type) {
        case KeycloakEventType.Ready:
          const readyArgs = typeEventArgs<ReadyArgs>(keycloakEvent.args);
          this.isAuthenticated.set(readyArgs);
          if (readyArgs) {
            void this.loadUserProfile();
          }
          console.log('[Keycloak] Ready - Authenticated:', readyArgs);
          break;

        case KeycloakEventType.AuthSuccess:
          this.isAuthenticated.set(true);
          void this.loadUserProfile();
          console.log('[Keycloak] Authentication successful');
          break;

        case KeycloakEventType.AuthLogout:
          this.isAuthenticated.set(false);
          this.userProfile$.next(null);
          console.log('[Keycloak] User logged out');
          break;

        case KeycloakEventType.AuthRefreshSuccess:
          console.log('[Keycloak] Token refreshed successfully');
          break;

        case KeycloakEventType.AuthRefreshError:
          console.error('[Keycloak] Token refresh failed');
          this.isAuthenticated.set(false);
          break;

        case KeycloakEventType.TokenExpired:
          console.warn('[Keycloak] Token expired');
          break;

        default:
          console.log('[Keycloak] Event:', keycloakEvent.type);
      }
    });
  }


  /**
   * Observable pour surveiller l'état d'authentification
   */
  isAuthenticated$(): Observable<boolean> {
    return from([this.keycloak.authenticated ?? false]);
  }

  /**
   * Connexion de l'utilisateur
   */
  login(): Promise<void> {
    return this.keycloak.login();
  }

  /**
   * Déconnexion de l'utilisateur
   */
  logout(): Promise<void> {
    this.userProfile$.next(null);
    return this.keycloak.logout();
  }

  /**
   * Obtenir le token d'accès
   */
  getToken(): string | undefined {
    return this.keycloak.token;
  }

  /**
   * Obtenir un token valide (avec renouvellement automatique si nécessaire)
   */
  async getValidToken(): Promise<string | null> {
    try {
      if (this.keycloak.isTokenExpired(30)) {
        await this.keycloak.updateToken(30);
      }
      return this.keycloak.token || null;
    } catch (error) {
      console.error('[Keycloak] Erreur lors du renouvellement du token:', error);
      await this.logout();
      return null;
    }
  }

  /**
   * Obtenir le nom d'utilisateur
   */
  getUsername(): string | undefined {
    const profile = this.getUserProfile();
    return profile?.username || this.keycloak.tokenParsed?.['preferred_username'];
  }

  /**
   * Obtenir les rôles utilisateur du realm
   */
  getUserRoles(): string[] {
    return this.keycloak.realmAccess?.roles ?? [];
  }


  /**
   * Vérifier si l'utilisateur a un rôle spécifique
   */
  hasRole(role: string): boolean {
    return this.keycloak.hasRealmRole(role);
  }


  /**
   * Vérifier si l'utilisateur a au moins un des rôles spécifiés
   */
  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  /**
   * Vérifier si l'utilisateur a tous les rôles spécifiés
   */
  hasAllRoles(roles: string[]): boolean {
    return roles.every(role => this.hasRole(role));
  }


  /**
   * Charger le profil utilisateur
   */
  private async loadUserProfile(): Promise<void> {
    if (this.isAuthenticated()) {
      try {
        const profile = await this.keycloak.loadUserProfile();
        this.userProfile$.next(profile);
      } catch (error) {
        console.error('[Keycloak] Erreur lors du chargement du profil:', error);
        this.userProfile$.next(null);
      }
    }
  }

  /**
   * Recharger le profil utilisateur
   */
  async refreshUserProfile(): Promise<KeycloakProfile | null> {
    await this.loadUserProfile();
    return this.getUserProfile();
  }

  /**
   * Obtenir le profil utilisateur observable
   */
  getUserProfile$(): Observable<KeycloakProfile | null> {
    return this.userProfile$.asObservable();
  }

  /**
   * Obtenir le profil utilisateur actuel
   */
  getUserProfile(): KeycloakProfile | null {
    return this.userProfile$.getValue();
  }

  /**
   * Obtenir l'email de l'utilisateur
   */
  getUserEmail(): string | undefined {
    const profile = this.getUserProfile();
    return profile?.email || this.keycloak.tokenParsed?.['email'];
  }

  /**
   * Obtenir le nom complet de l'utilisateur
   */
  getFullName(): string | undefined {
    const profile = this.getUserProfile();
    const tokenParsed = this.keycloak.tokenParsed;

    if (profile?.firstName && profile?.lastName) {
      return `${profile.firstName} ${profile.lastName}`;
    }

    if (tokenParsed?.['given_name'] && tokenParsed?.['family_name']) {
      return `${tokenParsed['given_name']} ${tokenParsed['family_name']}`;
    }

    return profile?.firstName ||
      profile?.lastName ||
      tokenParsed?.['name'] ||
      this.getUsername();
  }

  /**
   * Obtenir le prénom de l'utilisateur
   */
  getFirstName(): string | undefined {
    const profile = this.getUserProfile();
    return profile?.firstName || this.keycloak.tokenParsed?.['given_name'];
  }

  /**
   * Obtenir le nom de famille de l'utilisateur
   */
  getLastName(): string | undefined {
    const profile = this.getUserProfile();
    return profile?.lastName || this.keycloak.tokenParsed?.['family_name'];
  }

  /**
   * Rafraîchir le token
   */
  async refreshToken(minValidity: number = 30): Promise<boolean> {
    try {
      return await this.keycloak.updateToken(minValidity);
    } catch (error) {
      console.error('[Keycloak] Erreur lors du rafraîchissement du token:', error);
      return false;
    }
  }

  /**
   * Vérifier si le token est expiré
   */
  isTokenExpired(minValidity?: number): boolean {
    return this.keycloak.isTokenExpired(minValidity);
  }

  /**
   * Obtenir les informations du token parsé
   */
  getTokenParsed(): any {
    return this.keycloak.tokenParsed;
  }


}
