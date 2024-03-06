import { Location } from '@angular/common';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AbstractControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { throwError } from 'rxjs';
import { MockHuntService } from 'src/testing/hunt.service.mock';
import { AddHuntComponent } from './add-hunt.component';
import { HuntProfileComponent } from './hunt-profile.component';
import { HuntService } from './hunt.service';
//import { of } from 'rxjs';
//import { fakeAsync, flush, tic } from '@angular/core/testing'

describe('AddHuntComponent', () => {
  let addHuntComponent: AddHuntComponent;
  let addHuntForm: FormGroup;
  let fixture: ComponentFixture<AddHuntComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.overrideProvider(HuntService, { useValue: new MockHuntService() });
    TestBed.configureTestingModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        AddHuntComponent
    ],
}).compileComponents().catch(error => {
      expect(error).toBeNull();
    });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddHuntComponent);
    addHuntComponent = fixture.componentInstance;
    fixture.detectChanges();
    addHuntForm = addHuntComponent.addHuntForm;
    expect(addHuntForm).toBeDefined();
    expect(addHuntForm.controls).toBeDefined();
  });

  // Not terribly important; if the component doesn't create
  // successfully that will probably blow up a lot of things.
  // Including it, though, does give us confidence that our
  // our component definitions don't have errors that would
  // prevent them from being successfully constructed.
  it('should create the component and form', () => {
    expect(addHuntComponent).toBeTruthy();
    expect(addHuntForm).toBeTruthy();
  });

  // Confirms that an initial, empty form is *not* valid, so
  // people can't submit an empty form.
  it('form should be invalid when empty', () => {
    expect(addHuntForm.valid).toBeFalsy();
  });

  describe('The title field', () => {
    let titleControl: AbstractControl;

    beforeEach(() => {
      titleControl = addHuntComponent.addHuntForm.controls.title;
    });

    it('should not allow empty titles', () => {
      titleControl.setValue('');
      expect(titleControl.valid).toBeFalsy();
    });

    it('should be fine with "Chris Smith"', () => {
      titleControl.setValue('Chris Smith');
      expect(titleControl.valid).toBeTruthy();
    });

    it('should fail on single character names', () => {
      titleControl.setValue('x');
      expect(titleControl.valid).toBeFalsy();
      // Annoyingly, Angular uses lowercase 'l' here
      // when it's an upper case 'L' in `Validators.minLength(2)`.
      expect(titleControl.hasError('minlength')).toBeTruthy();
    });

    // In the real world, you'd want to be pretty careful about
    // setting upper limits on things like name lengths just
    // because there are people with really long names.
    it('should fail on really long names', () => {
      titleControl.setValue('x'.repeat(100));
      expect(titleControl.valid).toBeFalsy();
      // Annoyingly, Angular uses lowercase 'l' here
      // when it's an upper case 'L' in `Validators.maxLength(2)`.
      expect(titleControl.hasError('maxlength')).toBeTruthy();
    });

    it('should allow digits in the name', () => {
      titleControl.setValue('Bad2Th3B0ne');
      expect(titleControl.valid).toBeTruthy();
    });

    it('should fail if we provide an "existing" name', () => {
      // We're assuming that "abc123" and "123abc" already
      // exist so we disallow them.
      titleControl.setValue('abc123');
      expect(titleControl.valid).toBeFalsy();
      expect(titleControl.hasError('existingName')).toBeTruthy();

      titleControl.setValue('123abc');
      expect(titleControl.valid).toBeFalsy();
      expect(titleControl.hasError('existingName')).toBeTruthy();
    });
  });

  describe('getErrorMessage()', () => {
    it('should return the correct error message', () => {
      // The type statement is needed to ensure that `controlTitle` isn't just any
      // random string, but rather one of the keys of the `addUserValidationMessages`
      // map in the component.
      const controlTitle: keyof typeof addHuntComponent.addHuntValidationMessages = 'title';
      addHuntComponent.addHuntForm.get(controlTitle).setErrors({'required': true});
      expect(addHuntComponent.getErrorMessage(controlTitle)).toEqual('Title is required');

      // We don't need the type statement here because we're not using the
      // same (previously typed) variable. We could use a `let` and the type statement
      // if we wanted to create a new variable, though.
      // controlTitle = 'email';
      // addHuntComponent.addHuntForm.get(controlTitle).setErrors({'required': true});
      // expect(addHuntComponent.getErrorMessage(controlTitle)).toEqual('Email is required');

      // controlTitle = 'email';
      // addHuntComponent.addHuntForm.get(controlTitle).setErrors({'email': true});
      // expect(addHuntComponent.getErrorMessage(controlTitle)).toEqual('Email must be formatted properly');
    });

    it('should return "Unknown error" if no error message is found', () => {
      // The type statement is needed to ensure that `controlTitle` isn't just any
      // random string, but rather one of the keys of the `addUserValidationMessages`
      // map in the component.
      const controlTitle: keyof typeof addHuntComponent.addHuntValidationMessages = 'title';
      addHuntComponent.addHuntForm.get(controlTitle).setErrors({'unknown': true});
      expect(addHuntComponent.getErrorMessage(controlTitle)).toEqual('Unknown error');
    });
  })
});

describe('addHuntComponent#submitForm()', () => {
  let component: AddHuntComponent;
  let fixture: ComponentFixture<AddHuntComponent>;
  let huntService: HuntService;
  let location: Location;

  beforeEach(() => {
    TestBed.overrideProvider(HuntService, { useValue: new MockHuntService() });
    TestBed.configureTestingModule({
    imports: [
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatSelectModule,
        MatInputModule,
        BrowserAnimationsModule,
        RouterTestingModule.withRoutes([
            { path: 'hunts/1', component: HuntProfileComponent }
        ]),
        HttpClientTestingModule,
        AddHuntComponent, HuntProfileComponent
    ],
}).compileComponents().catch(error => {
      expect(error).toBeNull();
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddHuntComponent);
    component = fixture.componentInstance;
    huntService = TestBed.inject(HuntService);
    location = TestBed.inject(Location);
    // We need to inject the router and the HttpTestingController, but
    // never need to use them. So, we can just inject them into the TestBed
    // and ignore the returned values.
    TestBed.inject(Router);
    TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  beforeEach(() => {
    // Set up the form with valid values.
    // We don't actually have to do this, but it does mean that when we
    // check that `submitForm()` is called with the right arguments below,
    // we have some reason to believe that that wasn't passing "by accident".
    component.addHuntForm.controls.title.setValue('Chris Smith');
    // component.addHuntForm.controls.age.setValue(27);
    // component.addHuntForm.controls.company.setValue('Ohmnet');
    // component.addHuntForm.controls.email.setValue('this@that.com');
    // component.addHuntForm.controls.role.setValue('admin');
  });

  // This doesn't need `fakeAsync()`, `tick()`, or `flush() because the
  // error case doesn't navigate to another page. It just displays an error
  // message in the snackbar. So, we don't need to worry about the asynchronous
  // nature of navigation.
  it('should call addHunt() and handle error response', () => {
    // Save the original path so we can check that it doesn't change.
    const path = location.path();
    // A canned error response to be returned by the spy.
    const errorResponse = { status: 500, message: 'Server error' };
    // "Spy" on the `.addUser()` method in the hunt service. Here we basically
    // intercept any calls to that method and return the error response
    // defined above.
    const addUserSpy = spyOn(huntService, 'addHunt')
      .and
      .returnValue(throwError(() => errorResponse));
    component.submitForm();
    // Check that `.addUser()` was called with the form's values which we set
    // up above.
    expect(addUserSpy).toHaveBeenCalledWith(component.addHuntForm.value);
    // Confirm that we're still at the same path.
    expect(location.path()).toBe(path);
  });
});
