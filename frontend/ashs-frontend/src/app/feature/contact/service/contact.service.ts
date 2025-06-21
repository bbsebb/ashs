import {inject, Injectable} from '@angular/core';
import {EmailDTORequest} from './email-d-t-o-request';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {IContactService} from './i-contact.service';

@Injectable()
export class ContactService implements IContactService {
  private readonly httpClient = inject(HttpClient);


  sendEmail(emailRequest: EmailDTORequest): Observable<void> {
    return this.httpClient.post<void>('http://localhost:8081/sendEmail', emailRequest);
  }
}
