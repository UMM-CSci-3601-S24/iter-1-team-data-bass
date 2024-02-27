import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HuntProfileComponent } from './hunt-profile.component';

describe('HuntProfileComponent', () => {
  let component: HuntProfileComponent;
  let fixture: ComponentFixture<HuntProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HuntProfileComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HuntProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
