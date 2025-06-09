import {HalResource} from 'ngx-hal-forms';
import {HalResourceBuilder} from 'ngx-hal-forms';

export interface Coach extends HalResource {
  id?: number;
  name: string;
  surname: string;
  email: string;
  phone: string;
}

/**
 * Builder pour créer un objet Coach.
 */
export class CoachBuilder extends HalResourceBuilder<Coach> {
  /**
   * Définit l'identifiant du coach.
   * @param id Identifiant du coach
   */
  id(id: number): CoachBuilder {
    return this.property('id', id);
  }

  /**
   * Définit le nom du coach.
   * @param name Nom du coach
   */
  name(name: string): CoachBuilder {
    return this.property('name', name);
  }

  /**
   * Définit le prénom du coach.
   * @param surname Prénom du coach
   */
  surname(surname: string): CoachBuilder {
    return this.property('surname', surname);
  }

  /**
   * Définit l'email du coach.
   * @param email Email du coach
   */
  email(email: string): CoachBuilder {
    return this.property('email', email);
  }

  /**
   * Définit le téléphone du coach.
   * @param phone Téléphone du coach
   */
  phone(phone: string): CoachBuilder {
    return this.property('phone', phone);
  }

  /**
   * Construit et retourne l'objet Coach.
   * @throws Error si le nom, le prénom, l'email ou le téléphone ne sont pas définis
   */
  override build(): Coach {
    if (!this._properties.name) {
      throw new Error('name is required for Coach');
    }
    if (!this._properties.surname) {
      throw new Error('surname is required for Coach');
    }
    if (!this._properties.email) {
      throw new Error('email is required for Coach');
    }
    if (!this._properties.phone) {
      throw new Error('phone is required for Coach');
    }
    return super.build() as Coach;
  }
}
