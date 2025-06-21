import {EmailDTORequest} from './email-d-t-o-request';
import {Observable} from 'rxjs';
import {InjectionToken} from '@angular/core';

export interface IContactService {
  sendEmail(emailRequest: EmailDTORequest): Observable<void>;
}

export const CONTACT_SERVICE = new InjectionToken<IContactService>('CONTACT_SERVICE');
