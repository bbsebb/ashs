export interface Time {
  hour: number;
  minute: number;
  second: number;
  nano: number;
}

/**
 * Builder pour créer un objet Time.
 */
export class TimeBuilder {
  private _properties: Partial<Time> = {};

  /**
   * Définit l'heure.
   * @param hour Heure (0-23)
   */
  hour(hour: number): TimeBuilder {
    this._properties.hour = hour;
    return this;
  }

  /**
   * Définit les minutes.
   * @param minute Minutes (0-59)
   */
  minute(minute: number): TimeBuilder {
    this._properties.minute = minute;
    return this;
  }

  /**
   * Définit les secondes.
   * @param second Secondes (0-59)
   */
  second(second: number): TimeBuilder {
    this._properties.second = second;
    return this;
  }

  /**
   * Définit les nanosecondes.
   * @param nano Nanosecondes
   */
  nano(nano: number): TimeBuilder {
    this._properties.nano = nano;
    return this;
  }

  /**
   * Construit et retourne l'objet Time.
   * @throws Error si l'heure, les minutes, les secondes ou les nanosecondes ne sont pas définies
   */
  build(): Time {
    if (this._properties.hour === undefined) {
      throw new Error('hour is required for Time');
    }
    if (this._properties.minute === undefined) {
      throw new Error('minute is required for Time');
    }
    if (this._properties.second === undefined) {
      throw new Error('second is required for Time');
    }
    if (this._properties.nano === undefined) {
      throw new Error('nano is required for Time');
    }
    return { ...this._properties } as Time;
  }
}
