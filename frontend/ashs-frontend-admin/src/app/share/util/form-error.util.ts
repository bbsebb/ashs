import {AbstractControl, FormGroup} from '@angular/forms';

/**
 * Returns an error message for a form control based on its validation state.
 *
 * Checks for common validation errors and returns appropriate error messages:
 * - required: Field is mandatory
 * - email: Invalid email format
 * - invalidTimeSlot: Start time must be before end time
 * - default: Unknown error
 *
 * @param field The form control to check for errors
 * @returns A string containing the appropriate error message
 */
export function displayError(field: AbstractControl | null): string {

  if (field?.hasError('required')) {
    return 'Ce champ est obligatoire';
  } else if (field?.hasError('email')) {
    return 'Email invalide';
  } else if (field?.hasError('invalidTimeSlot')) {
    return `L'heure de début doit être antérieure à l'heure de fin.`;
  } else if (field?.hasError('maxlength')) {
    const maxLength = field.errors?.['maxlength']?.requiredLength;
    return `Ce champ ne doit pas dépasser ${maxLength} caractères`;
  } else if (field?.hasError('min')) {
    const min = field.errors?.['min']?.min;
    return `Ce champ ne doit pas être inférieur à ${min}`;
  } else if (field?.hasError('pattern')) {
    // Check if it's a phone field (this is a simple heuristic, might need adjustment)
    const controlName = field.parent?.controls ?
      Object.keys(field.parent.controls).find(key => field.parent?.get(key) === field) : '';
    if (controlName === 'phone') {
      return 'Le numéro de téléphone doit contenir entre 10 et 15 chiffres, avec éventuellement un "+" au début';
    }
    return 'Format invalide';
  } else {
    return 'Erreur inconnue';
  }
}

/**
 * Determines whether an error should be displayed for an Angular form control or group.
 *
 * How it works:
 * - If 'field' is a FormControl (simple field):
 *    Returns 'true' if the control is invalid and has either been modified (dirty) or touched (touched).
 * - If 'field' is a FormGroup (group of controls):
 *    Returns 'true' only if there is at least one global error
 *    (i.e., an error key that does not correspond to any child control name),
 *    and the group has been either modified (dirty) or touched (touched).
 *    This ensures that errors from group-level validators are displayed only when appropriate,
 *    and not errors that belong to child controls.
 * - Returns 'false' if the control is null or there is no error to display.
 *
 * This logic ensures that global errors (e.g., cross-field validation)
 * are only shown when present at the group level, while individual errors are shown for each specific control.
 *
 * @param field The control or control group to evaluate (FormControl or FormGroup)
 * @returns true if an error should be displayed, false otherwise
 */
export function hasError(field: AbstractControl | null): boolean {
  if (!field) return false;

  if (field instanceof FormGroup) {
    // Afficher une erreur uniquement si une erreur "globale" existe (pas sur les enfants individuellement)
    const errors = field.errors || {};
    const keys = Object.keys(errors);
    const childControls = Object.keys(field.controls);

    // Une erreur globale est une erreur dont la clé ne correspond à aucun enfant
    const hasGlobalError = keys.some(key => !childControls.includes(key));
    return hasGlobalError && (field.dirty || field.touched);
  }
  // Pour les FormControl classiques
  return field.invalid && (field.dirty || field.touched);
}
