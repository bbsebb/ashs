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
import {NGX_LOGGER, NgxLoggerService} from 'ngx-logger';

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
  private readonly logger = inject(NGX_LOGGER);
  disabledSubmitButton: WritableSignal<boolean> = signal(false);
  showProgressBar: WritableSignal<boolean> = signal(false);

  constructor() {
    this.logger.debug('Initialisation du composant Contact');
    this.contactForm = this.formBuilder.group({
      name: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]),
      email: this.formBuilder.control<string>('', [Validators.required, Validators.email]),
      message: this.formBuilder.control<string>('', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]),
    });
    this.logger.debug('Formulaire de contact initialisé avec les validateurs');
  }

  onSubmit() {
    this.logger.info('Tentative de soumission du formulaire de contact');

    if (this.contactForm.valid) {
      this.logger.debug('Formulaire valide, préparation de l\'envoi');
      let emailRequest = this.contactForm.getRawValue() as EmailDTORequest;

      this.logger.debug('Données du formulaire', {
        name: emailRequest.name,
        email: emailRequest.email,
        messageLength: emailRequest.message.length
      });

      this.disabledSubmitButton.set(true);
      this.showProgressBar.set(true);
      this.logger.debug('Interface utilisateur mise à jour pour l\'envoi (bouton désactivé, barre de progression affichée)');

      // Envoi du formulaire
      this.logger.info('Envoi du message de contact');
      this.contactService.sendEmail(emailRequest).subscribe({
        error: (err) => {
          this.logger.error('Erreur lors de l\'envoi du message de contact', err);
          this.notificationService.showError('Une erreur est survenue lors de l\'envoi du message');
          this.disabledSubmitButton.set(false);
          this.showProgressBar.set(false);
          this.logger.debug('Interface utilisateur réinitialisée après erreur');
        },
        next: () => {
          this.logger.info('Message de contact envoyé avec succès');
          this.contactForm.reset();
          this.formDirective().resetForm();
          this.contactForm.markAsPristine();
          this.contactForm.markAsUntouched();
          this.logger.debug('Formulaire réinitialisé après envoi réussi');

          this.notificationService.showSuccess('Votre message a bien été envoyé');
          this.disabledSubmitButton.set(false);
          this.showProgressBar.set(false);
          this.logger.debug('Interface utilisateur réinitialisée après succès');
        }
      });
    } else {
      this.logger.warn('Tentative de soumission d\'un formulaire invalide', {
        nameValid: this.contactForm.get('name')?.valid,
        emailValid: this.contactForm.get('email')?.valid,
        messageValid: this.contactForm.get('message')?.valid
      });
    }
  }

  getFormControlErrorText(ctrl: AbstractControl): string {
    this.logger.debug('Récupération du texte d\'erreur pour un contrôle de formulaire');

    let errorMessage: string;

    if (ctrl.hasError('required')) {
      errorMessage = "Ce champ est obligatoire";
    } else if (ctrl.hasError('email')) {
      errorMessage = "Adresse email invalide";
    } else if (ctrl.hasError('minlength')) {
      const minlength = ctrl.errors?.['minlength']?.requiredLength;
      errorMessage = `Il faut ${minlength} caractères minimum `;
    } else if (ctrl.hasError('maxlength')) {
      const maxlength = ctrl.errors?.['maxlength']?.requiredLength;
      errorMessage = `Il faut ${maxlength} caractères maximum `;
    } else {
      errorMessage = "Ce champs contient une erreur";
    }

    this.logger.debug('Texte d\'erreur déterminé', { errorMessage, errors: ctrl.errors });
    return errorMessage;
  }
}
