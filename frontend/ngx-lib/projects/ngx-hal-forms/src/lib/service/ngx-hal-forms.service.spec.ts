import { TestBed } from '@angular/core/testing';

import { NgxHalFormsService } from './ngx-hal-forms.service';

describe('NgxHalFormsService', () => {
  let service: NgxHalFormsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NgxHalFormsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
