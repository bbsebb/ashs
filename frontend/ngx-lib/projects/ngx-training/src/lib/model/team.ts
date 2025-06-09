import {Gender} from "./gender";
import {Category} from './category';
import {HalResource} from 'ngx-hal-forms';
import {HalResourceBuilder} from 'ngx-hal-forms';


export interface Team extends HalResource {
  id?: number;
  gender: Gender;
  category: Category;
  teamNumber: number;
}

/**
 * Builder pour créer un objet Team.
 */
export class TeamBuilder extends HalResourceBuilder<Team> {
  /**
   * Définit l'identifiant de l'équipe.
   * @param id Identifiant de l'équipe
   */
  id(id: number): TeamBuilder {
    return this.property('id', id);
  }

  /**
   * Définit le genre de l'équipe.
   * @param gender Genre de l'équipe
   */
  gender(gender: Gender): TeamBuilder {
    return this.property('gender', gender);
  }

  /**
   * Définit la catégorie de l'équipe.
   * @param category Catégorie de l'équipe
   */
  category(category: Category): TeamBuilder {
    return this.property('category', category);
  }

  /**
   * Définit le numéro de l'équipe.
   * @param teamNumber Numéro de l'équipe
   */
  teamNumber(teamNumber: number): TeamBuilder {
    return this.property('teamNumber', teamNumber);
  }

  /**
   * Construit et retourne l'objet Team.
   * @throws Error si le genre, la catégorie ou le numéro d'équipe ne sont pas définis
   */
  override build(): Team {
    if (!this._properties.gender) {
      throw new Error('gender is required for Team');
    }
    if (!this._properties.category) {
      throw new Error('category is required for Team');
    }
    if (!this._properties.teamNumber && this._properties.teamNumber !== 0) {
      throw new Error('teamNumber is required for Team');
    }
    return super.build() as Team;
  }
}
