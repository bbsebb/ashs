import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRoleCoachDialogComponent } from './add-role-coach-dialog.component';

describe('AddRoleCoachDialogComponent', () => {
  let component: AddRoleCoachDialogComponent;
  let fixture: ComponentFixture<AddRoleCoachDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddRoleCoachDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddRoleCoachDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
