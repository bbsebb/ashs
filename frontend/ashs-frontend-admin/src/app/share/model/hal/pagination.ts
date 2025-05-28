import {HalLink, HalLinks} from './hal';

/**
 * Métadonnées de pagination inspirées de la représentation "page" de Spring HATEOAS.
 */
export interface Pagination {
  page: {
    /**
     * Taille de la page (nombre d’éléments par page)
     */
    size: number;
    /**
     * Nombre total d’éléments.
     */
    totalElements: number;
    /**
     * Nombre total de pages.
     */
    totalPages: number;
    /**
     * Numéro de la page courante (généralement commençant à 0 ou 1 selon l’implémentation).
     */
    number: number;
  }
  _links: HalLinks & {
    next?: HalLink;
    prev?: HalLink;
    first?: HalLink;
    last?: HalLink;
  };
}
