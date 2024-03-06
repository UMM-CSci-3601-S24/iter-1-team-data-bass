import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerComponent } from './player.component';

describe('PlayerComponent', () => {
  let component: PlayerComponent;
  let fixture: ComponentFixture<PlayerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add a new textarea when addTextarea is called', () => {
    const initialLength = component.textAreasList.length;
    component.addTextarea();
    expect(component.textAreasList.length).toBe(initialLength + 1);
    expect(component.textAreasList[initialLength]).toBe('text_area' + (initialLength + 1));
  });

  it('should remove a textarea when removeTextArea is called', () => {
    // Add a textarea to the list
    component.addTextarea();
    const initialLength = component.textAreasList.length;

    // Remove the textarea we just added
    component.removeTextArea(initialLength - 1);

    expect(component.textAreasList.length).toBe(initialLength - 1);
  });
});
