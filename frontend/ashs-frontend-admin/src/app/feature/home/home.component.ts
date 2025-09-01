import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {RouterModule} from '@angular/router';
import {KeycloakService} from '@app/share/service/keycloak.service';

interface Feature {
  readonly title: string;
  readonly description: string;
  readonly icon: string;
  readonly route: string;
  readonly color: 'primary' | 'accent' | 'warn';
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    RouterModule
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private readonly keycloakService = inject(KeycloakService);

  // Utilisation des signaux Angular 19
  readonly isAuthenticated = this.keycloakService.isAuthenticated;
  readonly username = signal(this.keycloakService.getUsername() || '');

  // Computed signal pour l'état d'authentification
  readonly authenticationStatus = computed(() => ({
    isAuthenticated: this.isAuthenticated(),
    username: this.username(),
    displayName: this.username() || 'Utilisateur'
  }));

  readonly features: readonly Feature[] = [
    {
      title: 'Équipes',
      description: 'Gérez les équipes, leurs compositions et leurs informations.',
      icon: 'groups',
      route: '/teams',
      color: 'primary'
    },
    {
      title: 'Entraîneurs',
      description: 'Administrez les profils des entraîneurs et leurs assignations.',
      icon: 'sports',
      route: '/coaches',
      color: 'accent'
    },
    {
      title: 'Salles',
      description: 'Organisez les salles et leurs disponibilités.',
      icon: 'location_on',
      route: '/halls',
      color: 'warn'
    }
  ] as const;

  ngOnInit(): void {
    this.updateAuthenticationStatus();
  }

  private updateAuthenticationStatus(): void {
    const authenticated = this.keycloakService.isAuthenticated();
    this.isAuthenticated.set(authenticated);

    if (authenticated) {
      const userInfo = this.keycloakService.getUsername();
      this.username.set(userInfo || '');
    }
  }

  onLogin(): void {
    try {
      void this.keycloakService.login();
    } catch (error) {
      console.error('Erreur lors de la connexion:', error);
    }
  }

  onRegister(): void {
    try {
      // Keycloak registration - généralement géré via le flux d'inscription de Keycloak
      void this.keycloakService.login();
    } catch (error) {
      console.error('Erreur lors de l\'inscription:', error);
    }
  }

  trackByFeature(index: number, feature: Feature): string {
    return feature.title;
  }
}
