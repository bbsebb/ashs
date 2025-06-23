import {inject, Injectable} from '@angular/core';
import {EmailDTORequest} from './email-d-t-o-request';
import {Observable, switchMap} from 'rxjs';
import {IContactService} from './i-contact.service';
import {HalResource, NgxHalFormsService} from 'ngx-hal-forms';

@Injectable()
export class ContactService implements IContactService {
  private readonly halFormsService = inject(NgxHalFormsService);
  private readonly root = this.halFormsService.root


  sendEmail(emailRequest: EmailDTORequest): Observable<void> {
    return this.root.pipe(
      switchMap(root => this.halFormsService.follow<HalResource>(root, 'contact')),
      switchMap(contact => this.halFormsService.doAction<void>(contact, 'sendEmail', emailRequest))
    )
  }
}
