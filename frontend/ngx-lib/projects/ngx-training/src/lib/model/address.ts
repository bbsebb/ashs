export interface Address {
  street: string;
  city: string;
  postalCode: string;
  country: string;
}

/**
 * Builder pour créer un objet Address.
 */
export class AddressBuilder {
  private _properties: Partial<Address> = {};

  /**
   * Définit la rue de l'adresse.
   * @param street Rue de l'adresse
   */
  street(street: string): AddressBuilder {
    this._properties.street = street;
    return this;
  }

  /**
   * Définit la ville de l'adresse.
   * @param city Ville de l'adresse
   */
  city(city: string): AddressBuilder {
    this._properties.city = city;
    return this;
  }

  /**
   * Définit le code postal de l'adresse.
   * @param postalCode Code postal de l'adresse
   */
  postalCode(postalCode: string): AddressBuilder {
    this._properties.postalCode = postalCode;
    return this;
  }

  /**
   * Définit le pays de l'adresse.
   * @param country Pays de l'adresse
   */
  country(country: string): AddressBuilder {
    this._properties.country = country;
    return this;
  }

  /**
   * Construit et retourne l'objet Address.
   * @throws Error si la rue, la ville, le code postal ou le pays ne sont pas définis
   */
  build(): Address {
    if (!this._properties.street) {
      throw new Error('street is required for Address');
    }
    if (!this._properties.city) {
      throw new Error('city is required for Address');
    }
    if (!this._properties.postalCode) {
      throw new Error('postalCode is required for Address');
    }
    if (!this._properties.country) {
      throw new Error('country is required for Address');
    }
    return { ...this._properties } as Address;
  }
}
