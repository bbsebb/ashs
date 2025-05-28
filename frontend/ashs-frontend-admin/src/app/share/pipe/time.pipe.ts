import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'timeFormat',
  standalone: true,
})
export class TimePipe implements PipeTransform {

  // Fonction qui formate une chaîne "HH:mm:ss" en "HHhMM"
  transform(timeString: string): string {
    if (!timeString) {
      return '';  // Gérer les valeurs nulles ou undefined
    }

    const [hour, minute] = timeString.split(':').map(Number);
    return `${hour.toString().padStart(2, '0')}h${minute.toString().padStart(2, '0')}`;
  }
}
