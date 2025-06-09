import {TimeSlot} from "./time-slot";
import {Hall} from "./hall";
import {HalResource} from 'ngx-hal-forms';
import {HalResourceBuilder} from 'ngx-hal-forms';

export interface TrainingSession extends HalResource {
  id: number;
  timeSlot: TimeSlot;
  hall: Hall;
}

/**
 * Builder pour créer un objet TrainingSession.
 */
export class TrainingSessionBuilder extends HalResourceBuilder<TrainingSession> {
  /**
   * Définit l'identifiant de la séance d'entraînement.
   * @param id Identifiant de la séance d'entraînement
   */
  id(id: number): TrainingSessionBuilder {
    return this.property('id', id);
  }

  /**
   * Définit le créneau horaire de la séance d'entraînement.
   * @param timeSlot Créneau horaire
   */
  timeSlot(timeSlot: TimeSlot): TrainingSessionBuilder {
    return this.property('timeSlot', timeSlot);
  }

  /**
   * Définit la salle de la séance d'entraînement.
   * @param hall Salle
   */
  hall(hall: Hall): TrainingSessionBuilder {
    return this.property('hall', hall);
  }

  /**
   * Construit et retourne l'objet TrainingSession.
   * @throws Error si l'identifiant, le créneau horaire ou la salle ne sont pas définis
   */
  override build(): TrainingSession {
    if (!this._properties.id && this._properties.id !== 0) {
      throw new Error('id is required for TrainingSession');
    }
    if (!this._properties.timeSlot) {
      throw new Error('timeSlot is required for TrainingSession');
    }
    if (!this._properties.hall) {
      throw new Error('hall is required for TrainingSession');
    }
    return super.build() as TrainingSession;
  }
}
