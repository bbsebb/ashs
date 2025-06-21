import { TestBed } from '@angular/core/testing';

import { ContactStubService } from './contact-stub.service';

describe('ContactStubService', () => {
  let service: ContactStubService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactStubService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
