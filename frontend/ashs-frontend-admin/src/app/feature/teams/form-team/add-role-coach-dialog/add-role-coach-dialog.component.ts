import {Component, inject, signal, WritableSignal} from '@angular/core';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Role} from '@app/share/model/role';
import {Coach} from '@app/share/model/coach';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef} from '@angular/material/dialog';
import {MatError, MatFormField, MatLabel} from '@angular/material/input';
import {RoleToFrenchPipe} from '@app/share/pipe/role-to-french.pipe';
import {displayError, hasError} from '@app/share/validator/form-error.util';
import {MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {MatButton} from '@angular/material/button';
import {CoachService} from '@app/share/service/coach.service';
import {FormRoleCoachDTO} from '@app/share/service/dto/form-role-coach-d-t-o';

@Component({
  selector: 'app-add-role-coach-dialog',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatError,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    ReactiveFormsModule,
    RoleToFrenchPipe,
    MatFormField,

  ],
  templateUrl: './add-role-coach-dialog.component.html',
  styleUrl: './add-role-coach-dialog.component.css'
})
export class AddRoleCoachDialogComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly matRef = inject(MatDialogRef);
  private readonly coachService = inject(CoachService);
  coaches: WritableSignal<Coach[]> = signal([]);

  constructor() {
    this.coachService.getCoaches().subscribe(coaches => this.coaches.set(coaches))
  }

  addRoleCoachForm = this.formBuilder.group({
    role: this.formBuilder.control<Role>(Role.MAIN, Validators.required),
    coach: this.formBuilder.control<Coach | undefined>(undefined, Validators.required)
  });

  add() {
    if (this.addRoleCoachForm.invalid) {
      // Marquer tous les champs comme touchés pour afficher les erreurs dans le template
      this.addRoleCoachForm.markAllAsTouched();
      return;
    }
    const formResult = this.addRoleCoachForm.getRawValue() as FormRoleCoachDTO;
    this.matRef.close(formResult);
  }


  protected readonly Role = Role;
  protected readonly Object = Object;
  protected readonly hasError = hasError;
  protected readonly displayError = displayError;
}
