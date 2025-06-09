import {HalFormsDocument} from './ngx-hal-form-model';
import {Pagination} from './ngx-pagination.model';


/**
 * Représente une collection de ressources HAL paginées.
 *
 * Ce type combine les informations de pagination (Pagination) aux ressources HAL principales (HalResource).
 * Il contient également les ressources intégrées (_embedded) et les liens spécifiques (_links).
 *
 * @property _embedded - Ressources intégrées de la page actuelle, organisées par relation (rel).
 * @property _links - Liens associés à la collection, incluant les liens de navigation (next, prev, first, last).
 * @property next - Lien vers la page suivante, s'il existe.
 * @property prev - Lien vers la page précédente, s'il existe.
 * @property first - Lien vers la première page, s'il existe.
 * @property last - Lien vers la dernière page, s'il existe.
 */
export type AllHalResources<T extends HalResource = HalResource> = HalResource & {
  _embedded: {
    [rel: string]: T[];
  };
};

export type PaginatedHalResource<T extends HalResource> =
  AllHalResources<T> & Pagination;

/**
 * Représente un document HAL complet.
 * Il contient des liens (_links), des ressources intégrées (_embedded),
 * ainsi que les propriétés propres à la ressource.
 */
export type HalResource<T = {}> = T & HalFormsDocument & {
  /**
   * Liens associés à la ressource.
   */
  _links: HalLinks;
  /**
   * Ressources intégrées dans la représentation.
   */
  _embedded?: HalEmbedded;
}

/**
 * Collection de liens HAL.
 * Chaque clé correspond à une relation (rel), et la valeur est un ou plusieurs liens.
 */
export type HalLinks = {
  self: HalLink;
  [rel: string]: HalLink | HalLink[];
}


/**
 * Représente un lien HAL.
 */
export type HalLink = {
  /**
   * URI cible du lien.
   */
  href: string;
  /**
   * Indique si le lien est un URI template.
   */
  templated?: boolean;
  /**
   * Type MIME attendu lors de l'accès à la ressource cible.
   */
  type?: string;
  /**
   * URI indiquant que le lien est obsolète.
   */
  deprecation?: string;
  /**
   * Nom du lien, utile pour les liens de type CURIE.
   */
  name?: string;
  /**
   * URI du profil associé à la ressource cible.
   */
  profile?: string;
  /**
   * Titre du lien, à des fins d'affichage.
   */
  title?: string;
  /**
   * Langue du contenu de la ressource cible.
   */
  hreflang?: string;
  /**
   * Autres propriétés personnalisées.
   */
  [prop: string]: any;
}

/**
 * Ressources intégrées dans une représentation HAL.
 * Chaque clé correspond à une relation (rel), et la valeur est une ressource ou un tableau de ressources.
 */
export type HalEmbedded = {
  [rel: string]: HalResource | HalResource[];
}




