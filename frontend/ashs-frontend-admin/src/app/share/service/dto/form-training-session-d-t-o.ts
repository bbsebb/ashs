import {TimeSlot} from '@app/share/model/time-slot';
import {Hall} from '@app/share/model/hall';

export interface FormTrainingSessionDTO {
  id?: number;
  timeSlot: TimeSlot;
  hall: Hall;
}
