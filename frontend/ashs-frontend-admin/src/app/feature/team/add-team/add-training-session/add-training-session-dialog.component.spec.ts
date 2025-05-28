import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddTrainingSessionDialogComponent} from './add-training-session-dialog.component';

describe('AddTrainingSessionComponent', () => {
  let component: AddTrainingSessionDialogComponent;
  let fixture: ComponentFixture<AddTrainingSessionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddTrainingSessionDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AddTrainingSessionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
