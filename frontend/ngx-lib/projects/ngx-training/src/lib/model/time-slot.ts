import {DayOfWeek} from "./day-of-week";

export interface TimeSlot {
  id?: number;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}

/**
 * Builder pour créer un objet TimeSlot.
 */
export class TimeSlotBuilder {
  private _properties: Partial<TimeSlot> = {};

  /**
   * Définit l'identifiant du créneau horaire.
   * @param id Identifiant du créneau horaire
   */
  id(id: number): TimeSlotBuilder {
    this._properties.id = id;
    return this;
  }

  /**
   * Définit le jour de la semaine du créneau horaire.
   * @param dayOfWeek Jour de la semaine
   */
  dayOfWeek(dayOfWeek: DayOfWeek): TimeSlotBuilder {
    this._properties.dayOfWeek = dayOfWeek;
    return this;
  }

  /**
   * Définit l'heure de début du créneau horaire.
   * @param startTime Heure de début
   */
  startTime(startTime: string): TimeSlotBuilder {
    this._properties.startTime = startTime;
    return this;
  }

  /**
   * Définit l'heure de fin du créneau horaire.
   * @param endTime Heure de fin
   */
  endTime(endTime: string): TimeSlotBuilder {
    this._properties.endTime = endTime;
    return this;
  }

  /**
   * Construit et retourne l'objet TimeSlot.
   * @throws Error si le jour de la semaine, l'heure de début ou l'heure de fin ne sont pas définis
   */
  build(): TimeSlot {
    if (!this._properties.dayOfWeek) {
      throw new Error('dayOfWeek is required for TimeSlot');
    }
    if (!this._properties.startTime) {
      throw new Error('startTime is required for TimeSlot');
    }
    if (!this._properties.endTime) {
      throw new Error('endTime is required for TimeSlot');
    }

    return { ...this._properties } as TimeSlot;
  }
}
