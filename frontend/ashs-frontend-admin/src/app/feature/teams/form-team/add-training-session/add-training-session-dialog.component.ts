import {Component, inject} from '@angular/core';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {timeSlotValidator} from '@app/share/validator/time-slot.validator';
import {displayError, hasError} from '@app/share/util/form-error.util';
import {DayOfWeek, DayOfWeekPipe, FormTrainingSessionDTO, Hall, HallsStore} from 'ngx-training';

@Component({
  selector: 'app-add-training-session',
  imports: [
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    DayOfWeekPipe,
    MatInput,
    MatError
  ],
  templateUrl: './add-training-session-dialog.component.html',
  styleUrl: './add-training-session-dialog.component.css'
})
export class AddTrainingSessionDialogComponent {
  formBuild = inject(NonNullableFormBuilder);
  hallsStore = inject(HallsStore);
  matRef = inject(MatDialogRef);
  addTrainingSessionForm = this.formBuild.group({
    timeSlot: this.formBuild.group({
      dayOfWeek: this.formBuild.control<DayOfWeek | undefined>(undefined, Validators.required),
      startTime: this.formBuild.control<string>('', Validators.required),
      endTime: this.formBuild.control<string>('', Validators.required),
    }, {validators: timeSlotValidator()}),
    hall: this.formBuild.control<Hall | undefined>(undefined, Validators.required),
  })


  add() {
    if (this.addTrainingSessionForm.invalid) {
      // Marquer tous les champs comme touchés pour afficher les erreurs dans le template
      this.addTrainingSessionForm.markAllAsTouched();
      return;
    }
    const formResult = this.addTrainingSessionForm.getRawValue() as FormTrainingSessionDTO;
    this.matRef.close(formResult);
  }


  protected readonly Object = Object;
  protected readonly DayOfWeek = DayOfWeek;
  protected readonly hasError = hasError;
  protected readonly displayError = displayError;
}
