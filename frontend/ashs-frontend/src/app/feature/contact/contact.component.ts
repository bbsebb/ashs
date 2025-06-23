import {Component, inject, signal, viewChild, WritableSignal} from '@angular/core';
import {
  AbstractControl,
  FormGroup,
  FormGroupDirective,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {NgxNotificationService} from 'ngx-notification';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardFooter,
  MatCardHeader,
  MatCardTitle
} from '@angular/material/card';
import {MatError, MatFormField, MatLabel} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatButton} from '@angular/material/button';
import {EmailDTORequest} from './service/email-d-t-o-request';
import {CONTACT_SERVICE} from './service/i-contact.service';
import {ContactService} from './service/contact.service';

@Component({
  selector: 'app-contact',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatFormField,
    MatError,
    MatInput,
    MatProgressBar,
    MatCardFooter,
    MatCardActions,
    ReactiveFormsModule,
    MatButton,
    MatLabel,
    MatCardTitle
  ],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss',
  providers: [{
    provide: CONTACT_SERVICE,
    useClass: ContactService
  }]
})
export class ContactComponent {
  private readonly formDirective = viewChild.required<FormGroupDirective>('formDirective')
  contactForm!: FormGroup;
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly contactService = inject(CONTACT_SERVICE);
  private readonly notificationService = inject(NgxNotificationService);
  disabledSubmitButton: WritableSignal<boolean> = signal(false);
  showProgressBar: WritableSignal<boolean> = signal(false);

  constructor() {
    this.contactForm = this.formBuilder.group({
      name: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]),
      email: this.formBuilder.control<string>('', [Validators.required, Validators.email]),
      message: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]),
    });
  }


  onSubmit() {
    if (this.contactForm.valid) {
      let emailRequest = this.contactForm.getRawValue() as EmailDTORequest
      this.disabledSubmitButton.set(true);
      this.showProgressBar.set(true);
      // Envoi du formulaire
      this.contactService.sendEmail(emailRequest).subscribe(
        {
          error: () => {
            this.notificationService.showError('Une erreur est survenue lors de l\'envoi du message');
            this.disabledSubmitButton.set(false);
            this.showProgressBar.set(false);
          },
          next: () => {
            this.contactForm.reset();
            this.formDirective().resetForm();
            this.contactForm.markAsPristine();
            this.contactForm.markAsUntouched();
            this.notificationService.showSuccess('Votre message a bien été envoyé');
            this.disabledSubmitButton.set(false);
            this.showProgressBar.set(false);
          }
        });
    }
  }

  getFormControlErrorText(ctrl: AbstractControl): string {
    if (ctrl.hasError('required')) {
      return "Ce champ est obligatoire";
    } else if (ctrl.hasError('email')) {
      return "Adresse email invalide";
    } else if (ctrl.hasError('minlength')) {
      const minlength = ctrl.errors?.['minlength']?.requiredLength;
      return `Il faut ${minlength} caractères minimum `;
    } else if (ctrl.hasError('maxlength')) {
      const maxlength = ctrl.errors?.['maxlength']?.requiredLength;
      return `Il faut ${maxlength} caractères maximum `;
    } else {
      return "Ce champs contient une erreur";
    }
  }
}
