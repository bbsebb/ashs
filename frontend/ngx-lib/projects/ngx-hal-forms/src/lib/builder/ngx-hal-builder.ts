import {
  AllHalResources,
  HalEmbedded,
  HalLink,
  HalLinks,
  HalResource,
  PaginatedHalResource
} from '../model/ngx-hal.model';
import { HalFormsDocument, HalFormsTemplate } from '../model/ngx-hal-form-model';
import { Pagination } from '../model/ngx-pagination.model';

/**
 * Builder pour créer un objet HalLink.
 */
export class HalLinkBuilder {
  private _href?: string;
  private _templated?: boolean;
  private _type?: string;
  private _deprecation?: string;
  private _name?: string;
  private _profile?: string;
  private _title?: string;
  private _hreflang?: string;
  private _customProps: Record<string, any> = {};

  /**
   * Définit l'URI cible du lien.
   * @param href L'URI cible
   */
  href(href: string): HalLinkBuilder {
    this._href = href;
    return this;
  }

  /**
   * Indique si le lien est un URI template.
   * @param templated Valeur booléenne
   */
  templated(templated: boolean): HalLinkBuilder {
    this._templated = templated;
    return this;
  }

  /**
   * Définit le type MIME attendu lors de l'accès à la ressource cible.
   * @param type Type MIME
   */
  type(type: string): HalLinkBuilder {
    this._type = type;
    return this;
  }

  /**
   * Définit l'URI indiquant que le lien est obsolète.
   * @param deprecation URI d'obsolescence
   */
  deprecation(deprecation: string): HalLinkBuilder {
    this._deprecation = deprecation;
    return this;
  }

  /**
   * Définit le nom du lien, utile pour les liens de type CURIE.
   * @param name Nom du lien
   */
  name(name: string): HalLinkBuilder {
    this._name = name;
    return this;
  }

  /**
   * Définit l'URI du profil associé à la ressource cible.
   * @param profile URI du profil
   */
  profile(profile: string): HalLinkBuilder {
    this._profile = profile;
    return this;
  }

  /**
   * Définit le titre du lien, à des fins d'affichage.
   * @param title Titre du lien
   */
  title(title: string): HalLinkBuilder {
    this._title = title;
    return this;
  }

  /**
   * Définit la langue du contenu de la ressource cible.
   * @param hreflang Code de langue
   */
  hreflang(hreflang: string): HalLinkBuilder {
    this._hreflang = hreflang;
    return this;
  }

  /**
   * Ajoute une propriété personnalisée au lien.
   * @param key Clé de la propriété
   * @param value Valeur de la propriété
   */
  customProp(key: string, value: any): HalLinkBuilder {
    this._customProps[key] = value;
    return this;
  }

  /**
   * Construit et retourne l'objet HalLink.
   */
  build(): HalLink {
    if (!this._href) {
      throw new Error('href is required for HalLink');
    }

    return {
      href: this._href,
      ...(this._templated !== undefined && { templated: this._templated }),
      ...(this._type && { type: this._type }),
      ...(this._deprecation && { deprecation: this._deprecation }),
      ...(this._name && { name: this._name }),
      ...(this._profile && { profile: this._profile }),
      ...(this._title && { title: this._title }),
      ...(this._hreflang && { hreflang: this._hreflang }),
      ...this._customProps
    };
  }
}

/**
 * Builder pour créer un objet HalLinks.
 */
export class HalLinksBuilder {
  private _self: HalLink | undefined;
  private _links: Record<string, HalLink | HalLink[]> = {};

  /**
   * Définit le lien self.
   * @param selfLink Lien self
   */
  self(selfLink: HalLink): HalLinksBuilder {
    this._self = selfLink;
    return this;
  }

  /**
   * Définit le lien self à partir d'une URL.
   * @param href URL du lien self
   */
  selfUrl(href: string): HalLinksBuilder {
    this._self = new HalLinkBuilder().href(href).build();
    return this;
  }

  /**
   * Ajoute un lien à la collection.
   * @param rel Relation du lien
   * @param link Lien à ajouter
   */
  link(rel: string, link: HalLink): HalLinksBuilder {
    this._links[rel] = link;
    return this;
  }

  /**
   * Ajoute un tableau de liens à la collection.
   * @param rel Relation des liens
   * @param links Tableau de liens à ajouter
   */
  links(rel: string, links: HalLink[]): HalLinksBuilder {
    this._links[rel] = links;
    return this;
  }

  /**
   * Construit et retourne l'objet HalLinks.
   */
  build(): HalLinks {
    if (!this._self) {
      throw new Error('self link is required for HalLinks');
    }

    return {
      self: this._self,
      ...this._links
    };
  }
}

/**
 * Builder de base pour créer un objet HalResource.
 */
export class HalResourceBuilder<T = {}> {
  protected _properties: Partial<T> = {};
  protected _links: HalLinks | undefined;
  protected _embedded?: HalEmbedded;
  protected _templates?: { [templateKey: string]: HalFormsTemplate } = {};

  /**
   * Définit les liens de la ressource.
   * @param links Liens HAL
   */
  links(links: HalLinks): this {
    this._links = links;
    return this;
  }

  /**
   * Utilise un builder pour définir les liens de la ressource.
   * @param builderFn Fonction qui configure un HalLinksBuilder
   */
  withLinks(builderFn: (builder: HalLinksBuilder) => HalLinksBuilder): this {
    const builder = new HalLinksBuilder();
    this._links = builderFn(builder).build();
    return this;
  }

  /**
   * Définit les ressources intégrées.
   * @param embedded Ressources intégrées
   */
  embedded(embedded: HalEmbedded): this {
    this._embedded = embedded;
    return this;
  }

  /**
   * Ajoute une ressource intégrée.
   * @param rel Relation de la ressource
   * @param resource Ressource ou tableau de ressources
   */
  withEmbedded(rel: string, resource: HalResource | HalResource[]): this {
    if (!this._embedded) {
      this._embedded = {};
    }
    this._embedded[rel] = resource;
    return this;
  }

  /**
   * Définit un template de formulaire.
   * @param key Clé du template
   * @param template Template de formulaire
   */
  template(key: string, template: HalFormsTemplate): this {
    if (!this._templates) {
      this._templates = {};
    }
    this._templates[key] = template;
    return this;
  }

  /**
   * Définit une propriété de la ressource.
   * @param key Clé de la propriété
   * @param value Valeur de la propriété
   */
  property<K extends keyof T>(key: K, value: T[K]): this {
    this._properties[key] = value;
    return this;
  }

  /**
   * Définit plusieurs propriétés de la ressource.
   * @param properties Objet contenant les propriétés
   */
  properties(properties: Partial<T>): this {
    this._properties = { ...this._properties, ...properties };
    return this;
  }

  /**
   * Construit et retourne l'objet HalResource.
   */
  build(): HalResource<T> {
    if (!this._links) {
      throw new Error('links are required for HalResource');
    }

    const resource: HalResource<T> = {
      _links: this._links,
      ...(this._embedded && { _embedded: this._embedded }),
      ...(this._templates && { _templates: this._templates }),
      ...this._properties as T
    } as HalResource<T>;

    return resource;
  }
}

/**
 * Builder pour créer un objet AllHalResources.
 */
export class AllHalResourcesBuilder<T extends HalResource = HalResource> extends HalResourceBuilder<{}> {
  private _embeddedResources: Record<string, T[]> = {};

  /**
   * Ajoute une collection de ressources intégrées.
   * @param rel Relation des ressources
   * @param resources Tableau de ressources
   */
  embeddedResources(rel: string, resources: T[]): this {
    this._embeddedResources[rel] = resources;
    return this;
  }

  /**
   * Construit et retourne l'objet AllHalResources.
   */
  override build(): AllHalResources<T> {
    const baseResource = super.build();

    const allResources: AllHalResources<T> = {
      ...baseResource,
      _embedded: this._embeddedResources
    };

    return allResources;
  }
}

/**
 * Builder pour créer un objet PaginatedHalResource.
 */
export class PaginatedHalResourceBuilder<T extends HalResource> extends AllHalResourcesBuilder<T> {
  private _page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  } | undefined;

  /**
   * Définit les informations de pagination.
   * @param size Taille de la page
   * @param totalElements Nombre total d'éléments
   * @param totalPages Nombre total de pages
   * @param number Numéro de la page courante
   */
  pagination(size: number, totalElements: number, totalPages: number, number: number): this {
    this._page = {
      size,
      totalElements,
      totalPages,
      number
    };
    return this;
  }

  /**
   * Ajoute un lien de navigation "next".
   * @param href URL de la page suivante
   */
  nextLink(href: string): this {
    if (!this._links) {
      throw new Error('links must be set before adding navigation links');
    }
    this._links["next"] = new HalLinkBuilder().href(href).build();
    return this;
  }

  /**
   * Ajoute un lien de navigation "prev".
   * @param href URL de la page précédente
   */
  prevLink(href: string): this {
    if (!this._links) {
      throw new Error('links must be set before adding navigation links');
    }
    this._links["prev"] = new HalLinkBuilder().href(href).build();
    return this;
  }

  /**
   * Ajoute un lien de navigation "first".
   * @param href URL de la première page
   */
  firstLink(href: string): this {
    if (!this._links) {
      throw new Error('links must be set before adding navigation links');
    }
    this._links["first"] = new HalLinkBuilder().href(href).build();
    return this;
  }

  /**
   * Ajoute un lien de navigation "last".
   * @param href URL de la dernière page
   */
  lastLink(href: string): this {
    if (!this._links) {
      throw new Error('links must be set before adding navigation links');
    }
    this._links["last"] = new HalLinkBuilder().href(href).build();
    return this;
  }

  /**
   * Construit et retourne l'objet PaginatedHalResource.
   */
  override build(): PaginatedHalResource<T> {
    if (!this._page) {
      throw new Error('pagination information is required for PaginatedHalResource');
    }

    const allResources = super.build();

    const paginatedResource: PaginatedHalResource<T> = {
      ...allResources,
      page: this._page
    };

    return paginatedResource;
  }
}
