import { TestBed } from '@angular/core/testing';

import { IContactService } from './i-contact.service';

describe('IContactService', () => {
  let service: IContactService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IContactService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
