import {Component, computed, effect, inject, input, signal, Signal, WritableSignal} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {displayError, hasError} from '@app/share/util/form-error.util';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/divider';
import {ApiError} from '@app/share/model/api-error';
import {NotificationService} from '@app/share/service/notification.service';
import {Router, RouterLink} from '@angular/router';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {Coach, CoachesStore, CoachStore, CreateCoachDTORequest} from 'ngx-training';

@Component({
  selector: 'app-form-coach',
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
  templateUrl: './form-coach.component.html',
  styleUrl: './form-coach.component.css'
})
export class FormCoachComponent {
  protected readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly coachesStore = inject(CoachesStore);
  protected readonly coachStore = inject(CoachStore);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  uri = input<string>();
  isCreateSignal: Signal<boolean>;
  isSubmitting: WritableSignal<boolean> = signal(false);
  coachForm = this.createCoachForm();

  constructor() {
    effect(() => this.coachStore.uri = this.uri());
    this.isCreateSignal = computed(() => this.uri() === undefined);
    effect(() => this.coachForm = this.createCoachForm(this.coachStore.coach()));
  }

  createCoachForm(updatedCoach?: Coach) {
    const coach = {
      name: updatedCoach?.name ?? '',
      surname: updatedCoach?.surname ?? '',
      email: updatedCoach?.email ?? '',
      phone: updatedCoach?.phone ?? ''
    }
    return this.buildCoachForm(coach);
  }

  buildCoachForm(values: {
    name: string,
    surname: string,
    email: string,
    phone: string
  }) {
    return this.formBuilder.group({
      name: this.formBuilder.control<string>(values.name, Validators.required),
      surname: this.formBuilder.control<string>(values.surname, Validators.required),
      email: this.formBuilder.control<string>(values.email, [Validators.email]),
      phone: this.formBuilder.control<string>(values.phone, [Validators.pattern('\\+?[0-9]{10,15}')])
    });
  }

  submit() {
    if (!this.coachForm.invalid) {
      if (this.isCreateSignal()) {
        this.createCoach();
      } else {
        this.updateCoach();
      }
    }
  }

  createCoach() {
    this.isSubmitting.set(true);
    this.coachesStore.createCoach(this.coachForm.getRawValue() as CreateCoachDTORequest).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.notificationService.showSuccess(`Le coach a été créé`);
        this.goBack()
      },
      error: err => {
        this.isSubmitting.set(false);
        this.notificationService.showError(ApiError.of(err.error).getMessageForField('coachDTOCreateRequest'))
      }
    });
  }

  updateCoach() {
    this.isSubmitting.set(true);
    this.coachStore.updateCoach(this.coachForm.getRawValue() as CreateCoachDTORequest).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.notificationService.showSuccess(`Le coach a été modifié`);
      },
      error: err => {
        this.isSubmitting.set(false);
        this.notificationService.showError(ApiError.of(err.error).getGenericMessage())
      }
    });
  }

  protected readonly hasError = hasError;
  protected readonly displayError = displayError;

  goBack() {
    void this.router.navigate(['/coaches']);
  }
}
