import {Address} from "./address";
import {HalResource} from 'ngx-hal-forms';
import {HalResourceBuilder} from 'ngx-hal-forms';

export interface Hall extends HalResource {
  id: number;
  name: string;
  address: Address;
}

/**
 * Builder pour créer un objet Hall.
 */
export class HallBuilder extends HalResourceBuilder<Hall> {
  /**
   * Définit l'identifiant de la salle.
   * @param id Identifiant de la salle
   */
  id(id: number): HallBuilder {
    return this.property('id', id);
  }

  /**
   * Définit le nom de la salle.
   * @param name Nom de la salle
   */
  name(name: string): HallBuilder {
    return this.property('name', name);
  }

  /**
   * Définit l'adresse de la salle.
   * @param address Adresse de la salle
   */
  address(address: Address): HallBuilder {
    return this.property('address', address);
  }

  /**
   * Construit et retourne l'objet Hall.
   * @throws Error si l'identifiant, le nom ou l'adresse ne sont pas définis
   */
  override build(): Hall {
    if (!this._properties.id && this._properties.id !== 0) {
      throw new Error('id is required for Hall');
    }
    if (!this._properties.name) {
      throw new Error('name is required for Hall');
    }
    if (!this._properties.address) {
      throw new Error('address is required for Hall');
    }
    return super.build() as Hall;
  }
}
