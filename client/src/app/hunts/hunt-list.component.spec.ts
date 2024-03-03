import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Observable } from 'rxjs';
import { MockHuntService } from '../../testing/hunt.service.mock';
import { Hunt } from './hunt';
import { HuntCardComponent } from './hunt-card.component';
import { HuntListComponent } from './hunt-list.component';
import { HuntService } from './hunt.service';

const COMMON_IMPORTS: unknown[] = [
  FormsModule,
  MatCardModule,
  MatFormFieldModule,
  MatSelectModule,
  MatOptionModule,
  MatButtonModule,
  MatInputModule,
  MatExpansionModule,
  MatTooltipModule,
  MatListModule,
  MatDividerModule,
  MatRadioModule,
  MatIconModule,
  MatSnackBarModule,
  BrowserAnimationsModule,
  RouterTestingModule,
];

describe('Hunt list', () => {

  let huntList: HuntListComponent;
  let fixture: ComponentFixture<HuntListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [COMMON_IMPORTS, HuntListComponent, HuntCardComponent],
    // providers:    [ HuntService ]  // NO! Don't provide the real service!
    // Provide a test-double instead
    providers: [{ provide: HuntService, useValue: new MockHuntService() }]
});
  });

  // This constructs the `huntList` (declared
  // above) that will be used throughout the tests.
  beforeEach(waitForAsync(() => {
  // Compile all the components in the test bed
  // so that everything's ready to go.
    TestBed.compileComponents().then(() => {
      /* Create a fixture of the HuntListComponent. That
       * allows us to get an instance of the component
       * (huntList, below) that we can control in
       * the tests.
       */
      fixture = TestBed.createComponent(HuntListComponent);
      huntList = fixture.componentInstance;
      /* Tells Angular to sync the data bindings between
       * the model and the DOM. This ensures, e.g., that the
       * `huntList` component actually requests the list
       * of hunts from the `MockHuntService` so that it's
       * up to date before we start running tests on it.
       */
      fixture.detectChanges();
    });
  }));

  it('contains all the hunts', () => {
    expect(huntList.serverFilteredHunts.length).toBe(1);
  });

  it('contains a hunt named \'hunt\'', () => {
    expect(huntList.serverFilteredHunts.some((hunt: Hunt) => hunt.title === 'hunt')).toBe(false);
  });

  // it('contain a hunt named \'Jamie\'', () => {
  //   expect(huntList.serverFilteredHunts.some((hunt: Hunt) => hunt.name === 'Jamie')).toBe(true);
  // });

  // it('doesn\'t contain a hunt named \'Santa\'', () => {
  //   expect(huntList.serverFilteredHunts.some((hunt: Hunt) => hunt.name === 'Santa')).toBe(false);
  // });

  // it('has two hunts that are 37 years old', () => {
  //   expect(huntList.serverFilteredHunts.filter((hunt: Hunt) => hunt.age === 37).length).toBe(2);
  // });
});

/*
 * This test is a little odd, but illustrates how we can use stubs
 * to create mock objects (a service in this case) that be used for
 * testing. Here we set up the mock HuntService (HuntServiceStub) so that
 * _always_ fails (throws an exception) when you request a set of hunts.
 */
describe('Misbehaving hunt List', () => {
  let huntList: HuntListComponent;
  let fixture: ComponentFixture<HuntListComponent>;

  let HuntServiceStub: {
    getHunts: () => Observable<Hunt[]>;
  };

  beforeEach(() => {
    // stub HuntService for test purposes
    HuntServiceStub = {
      getHunts: () => new Observable(observer => {
        observer.error('getHunts() Observer generates an error');
      }),
    };

    TestBed.configureTestingModule({
    imports: [COMMON_IMPORTS, HuntListComponent],
    // providers:    [ HuntService ]  // NO! Don't provide the real service!
    // Provide a test-double instead
    providers: [{ provide: HuntService, useValue: HuntServiceStub }]
});
  });

  // Construct the `huntList` used for the testing in the `it` statement
  // below.
  beforeEach(waitForAsync(() => {
    TestBed.compileComponents().then(() => {
      fixture = TestBed.createComponent(HuntListComponent);
      huntList = fixture.componentInstance;
      fixture.detectChanges();
    });
  }));

  it('generates an error if we don\'t set up a huntListService', () => {
    const mockedMethod = spyOn(huntList, 'getHuntsFromServer').and.callThrough();
    // Since calling either gethunts() or gethuntsFiltered() return
    // Observables that then throw exceptions, we don't expect the component
    // to be able to get a list of hunts, and serverFilteredHunts should
    // be undefined.
    expect(huntList.serverFilteredHunts)
      .withContext('service can\'t give values to the list if it\'s not there')
      .toBeUndefined();
    expect(huntList.getHuntsFromServer)
      .withContext('will generate the right error if we try to getHuntsFromServer')
      .toThrow();
    expect(mockedMethod)
      .withContext('will be called')
      .toHaveBeenCalled();
    expect(huntList.errMsg)
      .withContext('the error message will be')
      .toContain('Problem contacting the server – Error Code:');
      console.log(huntList.errMsg);
  });
});
