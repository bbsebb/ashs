import {Injectable} from '@angular/core';
import {IContactService} from './i-contact.service';
import {EmailDTORequest} from './email-d-t-o-request';
import {delay, of, tap} from 'rxjs';

@Injectable()
export class ContactStubService implements IContactService {

  sendEmail(emailRequest: EmailDTORequest) {
    // Stub : retourne simplement un Observable simulant un appel HTTP
    return of(void 0).pipe(
      delay(2000),
      tap(() => console.log('Envoi de l\'email', emailRequest))
    );
  }


}
