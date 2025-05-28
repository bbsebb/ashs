import {DayOfWeek} from "./day-of-week";

export interface TimeSlot {
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;

}
