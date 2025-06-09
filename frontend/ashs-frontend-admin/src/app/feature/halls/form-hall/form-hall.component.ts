import {Component, computed, effect, inject, input, signal, Signal, WritableSignal} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {displayError, hasError} from '@app/share/util/form-error.util';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';


import {NotificationService} from '@app/share/service/notification.service';
import {Router, RouterLink} from '@angular/router';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {CreateHallDTORequest, Hall, HallsStore, HallStore, UpdateHallDTORequest} from 'ngx-training';
import {NgxApiError} from 'ngx-hal-forms';


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
    MatDivider,
    MatProgressSpinner,
    RouterLink
  ],
  templateUrl: './form-hall.component.html',
  styleUrl: './form-hall.component.css'
})
export class FormHallComponent {
  protected readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly hallsStore = inject(HallsStore);
  protected readonly hallStore = inject(HallStore);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  uri = input<string>();
  isCreateSignal: Signal<boolean>;
  isSubmitting: WritableSignal<boolean> = signal(false);
  hallForm = this.createHallForm();

  constructor() {
    effect(() => this.hallStore.uri = this.uri());
    this.isCreateSignal = computed(() => this.uri() === undefined);
    effect(() => this.hallForm = this.createHallForm(this.hallStore.hall()));
  }

  createHallForm(updatedHall?: Hall) {
    const hall = {
      name: updatedHall?.name ?? '',
      address: {
        street: updatedHall?.address?.street ?? '',
        city: updatedHall?.address.city ?? 'Hoenheim',
        postalCode: updatedHall?.address.postalCode ?? '67800',
        country: updatedHall?.address.country ?? 'France'
      }
    }
    return this.buildHallForm(hall);
  }


  buildHallForm(values: {
    name: string,
    address: { street: string, city: string, postalCode: string, country: string }
  }) {
    return this.formBuilder.group({
      name: this.formBuilder.control<string>(values.name, [Validators.required, Validators.maxLength(50)]),
      address: this.formBuilder.group({
        street: this.formBuilder.control<string>(values.address.street, Validators.required),
        city: this.formBuilder.control<string>(values.address.city, Validators.required),
        postalCode: this.formBuilder.control<string>(values.address.postalCode, Validators.required),
        country: this.formBuilder.control<string>(values.address.country, Validators.required)
      })
    });
  }


  submit() {
    if (!this.hallForm.invalid) {
      if (this.isCreateSignal()) {
        this.createHall();
      } else {
        this.updateHall();
      }

    }
  }

  createHall() {
    this.isSubmitting.set(true);
    this.hallsStore.createHall(this.hallForm.getRawValue() as CreateHallDTORequest).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.notificationService.showSuccess(`La salle a été crée`);
        this.goBack()
      },
      error: err => {
        this.isSubmitting.set(false);
        this.notificationService.showError(NgxApiError.of(err.error).getMessageForField('hallDTOCreateRequest'))
      }
    });
  }

  updateHall() {
    this.isSubmitting.set(true);
    this.hallStore.updateHall(this.hallForm.getRawValue() as UpdateHallDTORequest).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.notificationService.showSuccess(`La salle a été modifiée`);
      },
      error: err => {
        this.isSubmitting.set(false);
        this.notificationService.showError(NgxApiError.of(err.error).getGenericMessage())
      }
    });
  }

  protected readonly hasError = hasError;
  protected readonly displayError = displayError;

  goBack() {
    void this.router.navigate(['/halls']);
  }
}
