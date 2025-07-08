import {inject, Injectable} from '@angular/core';
import {EmailDTORequest} from './email-d-t-o-request';
import {Observable, switchMap, tap} from 'rxjs';
import {IContactService} from './i-contact.service';
import {HalResource, NgxHalFormsService} from 'ngx-hal-forms';
import {NGX_LOGGER} from 'ngx-logger';

@Injectable()
export class ContactService implements IContactService {
  private readonly logger = inject(NGX_LOGGER);
  private readonly halFormsService = inject(NgxHalFormsService);
  private readonly root = this.halFormsService.root


  sendEmail(emailRequest: EmailDTORequest): Observable<void> {
    return this.root.pipe(
      switchMap(root => this.halFormsService.follow<HalResource>(root, 'contact')),
      tap((contact) => this.logger.debug("récuperation de l'endpoint contact")),
      switchMap(contact => this.halFormsService.doAction<void>(contact, 'sendEmail', emailRequest)),
      tap(() => this.logger.debug('Envoi de l\'email', JSON.stringify(emailRequest))),
    )
  }
}
