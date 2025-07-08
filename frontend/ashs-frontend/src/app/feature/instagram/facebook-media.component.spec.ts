import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FacebookMediaComponent} from './facebook-media.component';

describe('InstagramComponent', () => {
  let component: FacebookMediaComponent;
  let fixture: ComponentFixture<FacebookMediaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacebookMediaComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(FacebookMediaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
