import {HalResource} from 'ngx-hal-forms';
import {HalResourceBuilder} from 'ngx-hal-forms';
import {Role} from './role';
import {Coach} from './coach';
import {Team} from './team';

export interface RoleCoach extends HalResource {
  id: number;
  role: Role;
  coach: Coach;
  team: Team;
}

/**
 * Builder pour créer un objet RoleCoach.
 */
export class RoleCoachBuilder extends HalResourceBuilder<RoleCoach> {
  /**
   * Définit l'identifiant du rôle de coach.
   * @param id Identifiant du rôle de coach
   */
  id(id: number): RoleCoachBuilder {
    return this.property('id', id);
  }

  /**
   * Définit le rôle.
   * @param role Rôle
   */
  role(role: Role): RoleCoachBuilder {
    return this.property('role', role);
  }

  /**
   * Définit le coach.
   * @param coach Coach
   */
  coach(coach: Coach): RoleCoachBuilder {
    return this.property('coach', coach);
  }

  /**
   * Définit l'équipe.
   * @param team Équipe
   */
  team(team: Team): RoleCoachBuilder {
    return this.property('team', team);
  }

  /**
   * Construit et retourne l'objet RoleCoach.
   * @throws Error si l'identifiant, le rôle, le coach ou l'équipe ne sont pas définis
   */
  override build(): RoleCoach {
    if (!this._properties.id && this._properties.id !== 0) {
      throw new Error('id is required for RoleCoach');
    }
    if (!this._properties.role) {
      throw new Error('role is required for RoleCoach');
    }
    if (!this._properties.coach) {
      throw new Error('coach is required for RoleCoach');
    }
    if (!this._properties.team) {
      throw new Error('team is required for RoleCoach');
    }
    return super.build() as RoleCoach;
  }
}
