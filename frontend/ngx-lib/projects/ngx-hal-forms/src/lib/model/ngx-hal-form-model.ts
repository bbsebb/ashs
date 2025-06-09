/**
 * Document HAL-FORMS complet.
 *
 * Un document HAL-FORMS possède obligatoirement deux objets de niveau supérieur :
 * - _links : la collection de liens (avec au moins le lien "self")
 * - _templates : la collection de formulaires (actions) disponibles
 */
export type HalFormsDocument = {

  _templates?: { [templateKey: string]: HalFormsTemplate };
}


/**
 * Représente un template (formulaire ou action) HAL‑FORMS.
 *
 * Celui-ci décrit notamment la méthode HTTP (method), le type de contenu attendu,
 * les éventuels paramètres (properties) et un lien cible (target) si différent du "self".
 */
export type HalFormsTemplate = {
  /**
   * Identifiant unique du template (souvent "default")
   */
  key: string;
  /**
   * Titre humain lisible pour identifier le formulaire.
   */
  title?: string;
  /**
   * Méthode HTTP à utiliser pour la soumission (GET, POST, PUT, PATCH, DELETE…)
   */
  method: "GET" | "POST" | "PUT" | "PATCH" | "DELETE" | "HEAD" | "OPTIONS" | "CONNECT" | "TRACE"
    | "SEARCH" | "LOCK" | "UNLOCK" | "PROPFIND" | "PROPPATCH";
  /**
   * Type de contenu à utiliser pour la requête.
   * Par défaut, "application/json" si non précisée.
   */
  contentType?: string;
  /**
   * (Optionnel) URL de cible sur laquelle la soumission doit être effectuée.
   * Si présent, il correspond à la propriété cible.
   */
  target?: string;
  /**
   * Liste des propriétés attendues dans le formulaire.
   * Si absent ou vide, la transition ne requiert pas de paramètres.
   */
  properties?: HalFormsProperty[];
}

/**
 * Représente une propriété (un champ de formulaire) dans un template HAL‑FORMS.
 *
 * On y retrouve des attributs "core" comme "name" et "required" et divers attributs additionnels
 * pour contrôler la présentation et la validation (regex, placeholder, etc.).
 */
export type HalFormsProperty = {
  /**
   * Nom de la propriété (obligatoire)
   */
  name: string;
  /**
   * Invite pour l’utilisateur (par défaut on utilise le nom de la propriété)
   */
  prompt?: string;
  /**
   * Détermine si le champ est en lecture seule.
   */
  readOnly?: boolean;
  /**
   * Expression régulière servant à valider la valeur.
   */
  regex?: string;
  /**
   * Indique si le champ est requis.
   */
  required?: boolean;
  /**
   * Indique si la valeur fournie est une URI Template à résoudre.
   */
  templated?: boolean;
  /**
   * Valeur par défaut (si absente, on la considère vide)
   */
  value?: string;

  // Attributs supplémentaires optionnels pour la présentation et la validation
  cols?: number;
  max?: number;
  maxLength?: number;
  min?: number;
  minLength?: number;
  placeholder?: string;
  rows?: number;
  step?: number | string;
  /**
   * Type de saisie pour le champ.
   * Les valeurs possibles reprennent les types d’input HTML.
   */
  type?: 'hidden' | 'text' | 'textarea' | 'search' | 'tel' | 'url' | 'email' | 'password' | 'date' | 'month' | 'week' | 'time' | 'datetime-local' | 'number' | 'range' | 'color';
  /**
   * Options pour une saisie à choix limité.
   */
  options?: HalFormsOptions;
  // Autorise d’autres propriétés spécifiques ou futures sans devoir lever d'erreur
  [prop: string]: any;
}

/**
 * Représente la structure d’options (liste de valeurs) pour une propriété.
 *
 * Cela permet de définir, par exemple, une liste déroulante ou des boutons radio,
 * avec la possibilité d’indiquer la valeur sélectionnée, les valeurs disponibles en ligne ou par référence,
 * ainsi que quelques contrôles supplémentaires (nombre minimum/maximal d’items, etc.).
 */
export interface HalFormsOptions {
  /**
   * Valeurs pré-sélectionnées (si le formulaire contient déjà des valeurs par défaut)
   */
  selectedValues?: string[];
  /**
   * Liste de valeurs possibles intégrée directement.
   * Chaque valeur peut être une chaîne ou un objet avec un "prompt" et une "value".
   */
  inline?: Array<string | { prompt: string; value: string }>;
  /**
   * Définit un lien pointant vers une ressource externe contenant la liste des valeurs possibles.
   */
  link?: {
    href: string;
    templated?: boolean;
    type?: string;
  };
  /**
   * Nom du champ dans l’objet retourné par l’option (pour l’invite).
   * Par défaut : "prompt".
   */
  promptField?: string;
  /**
   * Nom du champ dans l’objet retourné par l’option (pour la valeur).
   * Par défaut : "value".
   */
  valueField?: string;
  /**
   * Nombre minimum d’items qui doivent être sélectionnés.
   */
  minItems?: number;
  /**
   * Nombre maximum d’items que l’utilisateur peut sélectionner.
   */
  maxItems?: number;
}
