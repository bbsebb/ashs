import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormHallComponent } from './form-hall.component';

describe('FormHallComponent', () => {
  let component: FormHallComponent;
  let fixture: ComponentFixture<FormHallComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormHallComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormHallComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
