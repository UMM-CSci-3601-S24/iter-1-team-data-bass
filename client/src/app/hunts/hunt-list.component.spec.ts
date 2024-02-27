import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HuntListComponent } from './hunt-list.component';

describe('HuntListComponent', () => {
  let component: HuntListComponent;
  let fixture: ComponentFixture<HuntListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HuntListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HuntListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
