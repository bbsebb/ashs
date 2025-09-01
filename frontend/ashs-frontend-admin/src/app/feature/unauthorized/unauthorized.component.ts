import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {KeycloakService} from '@app/share/service/keycloak.service';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './unauthorized.component.html',
  styleUrl: './unauthorized.component.css'
})
export class UnauthorizedComponent {
  private keycloakService = inject(KeycloakService);
  private router = inject(Router);

  /**
   * Récupère le nom d'utilisateur pour l'affichage
   */
  getUserName(): string {
    const profile = this.keycloakService.getUserProfile();
    return profile?.firstName && profile?.lastName
      ? `${profile.firstName} ${profile.lastName}`
      : profile?.username || 'Utilisateur';
  }

  /**
   * Déconnecte l'utilisateur
   */
  logout(): void {
    void this.keycloakService.logout();
  }

  /**
   * Retourne à l'accueil
   */
  goHome(): void {
    void this.router.navigate(['/home']);
  }
}
