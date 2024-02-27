import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HuntCardComponent } from './hunt-card.component';

describe('HuntCardComponent', () => {
  let component: HuntCardComponent;
  let fixture: ComponentFixture<HuntCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HuntCardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HuntCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
