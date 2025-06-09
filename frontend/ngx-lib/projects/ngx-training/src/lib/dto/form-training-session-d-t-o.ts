import {TimeSlot} from '../model/time-slot';
import {Hall} from '../model/hall';


export interface FormTrainingSessionDTO {
  id?: number;
  timeSlot: TimeSlot;
  hall: Hall;
}
