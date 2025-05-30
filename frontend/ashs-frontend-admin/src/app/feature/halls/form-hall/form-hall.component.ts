import {Component, inject} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {displayError, hasError} from '@app/share/validator/form-error.util';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {CreateHallDTORequest} from '@app/share/service/dto/create-hall-d-t-o-request';
import {HallsStore} from '@app/share/store/halls.store';
import {ApiError} from '@app/share/model/api-error';
import {NotificationService} from '@app/share/service/notification.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-form-hall',
  imports: [
    MatFormField,
    MatInput,
    ReactiveFormsModule,
    MatError,
    MatButton,
    MatIcon,
    MatLabel,
    MatDivider
  ],
  templateUrl: './form-hall.component.html',
  styleUrl: './form-hall.component.css'
})
export class FormHallComponent {
  protected readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly hallsStore = inject(HallsStore);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  hallForm = this.formBuilder.group({
    name: this.formBuilder.control<string>('', Validators.required),
    address: this.formBuilder.group({
      street: this.formBuilder.control<string>('', Validators.required),
      city: this.formBuilder.control<string>('Hoenheim', Validators.required),
      postalCode: this.formBuilder.control<string>('67800', Validators.required),
      country: this.formBuilder.control<string>('France', Validators.required)
    })
  });

  submit() {
    if (!this.hallForm.invalid) {
      this.hallsStore.createHall(this.hallForm.getRawValue() as CreateHallDTORequest).subscribe({
        next: () => this.notificationService.showSuccess(`La salle a été crée`),
        error: err => this.notificationService.showError(ApiError.of(err.error).getMessageForField('hallDTOCreateRequest'))
      });
    }
  }

  protected readonly hasError = hasError;
  protected readonly displayError = displayError;

  goBack() {
    void this.router.navigate(['/halls']);
  }
}
