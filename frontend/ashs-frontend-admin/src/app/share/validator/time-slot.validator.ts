import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';

export function timeSlotValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {

    const startTime = control.get('startTime')?.value;
    const endTime = control.get('endTime')?.value;



    if (startTime && endTime && startTime >= endTime) {
      return { invalidTimeSlot: 'L\'heure de début doit être antérieure à l\'heure de fin.' };
    }

    return null;
  };
}
