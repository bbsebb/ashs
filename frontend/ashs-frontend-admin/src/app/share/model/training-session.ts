import {TimeSlot} from "./time-slot";
import {Hall} from "./hall";
import {HalResource} from './hal/hal';


export interface TrainingSession extends HalResource {
  id: number;
  timeSlot: TimeSlot;
  hall: Hall;
}
