import { Component } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { HuntService } from 'src/app/hunts/hunt.service';
import { NgFor } from '@angular/common';


@Component({
  selector: 'app-add-hunt',
  standalone: true,
  imports: [NgFor, FormsModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatOptionModule, MatButtonModule],
  templateUrl: './add-hunt.component.html',
  styleUrl: './add-hunt.component.scss'
})
export class AddHuntComponent {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    textAreasList:any = [];

    addTextarea(){
        this.textAreasList.push('text_area'+ (this.textAreasList.length + 1));
    }


    removeTextArea(index){
        this.textAreasList.splice(index, 1);
    }

  addHuntForm = new FormGroup({
    // We allow alphanumeric input and limit the length for name.
    title: new FormControl('', Validators.compose([
      Validators.required,
      Validators.minLength(2),
      // In the real world you'd want to be very careful about having
      // an upper limit like this because people can sometimes have
      // very long names. This demonstrates that it's possible, though,
      // to have maximum length limits.
      Validators.maxLength(50),
      (fc) => {
        if (fc.value.toLowerCase() === 'abc123' || fc.value.toLowerCase() === '123abc') {
          return ({existingName: true});
        } else {
          return null;
        }
      },
    ])),

    hostid: new FormControl('', Validators.compose([
      Validators.required,
      Validators.minLength(2),
      // In the real world you'd want to be very careful about having
      // an upper limit like this because people can sometimes have
      // very long names. This demonstrates that it's possible, though,
      // to have maximum length limits.
      Validators.maxLength(50),
      (fc) => {
        if (fc.value.toLowerCase() === 'abc123' || fc.value.toLowerCase() === '123abc') {
          return ({existingName: true});
        } else {
          return null;
        }
      },
    ])),

    description: new FormControl('', Validators.compose([
      Validators.required,
      Validators.minLength(2),
      // In the real world you'd want to be very careful about having
      // an upper limit like this because people can sometimes have
      // very long names. This demonstrates that it's possible, though,
      // to have maximum length limits.
      Validators.maxLength(50),
      (fc) => {
        if (fc.value.toLowerCase() === 'abc123' || fc.value.toLowerCase() === '123abc') {
          return ({existingName: true});
        } else {
          return null;
        }
      },
    ])),


    // // Since this is for a company, we need workers to be old enough to work, and probably not older than 200.
    // age: new FormControl<number>(null, Validators.compose([
    //   Validators.required,
    //   Validators.min(15),
    //   Validators.max(200),
    //   // In the HTML, we set type="number" on this field. That guarantees that the value of this field is numeric,
    //   // but not that it's a whole number. (The user could still type -27.3232, for example.) So, we also need
    //   // to include this pattern.
    //   Validators.pattern('^[0-9]+$')
    // ])),

    // // We don't care much about what is in the company field, so we just add it here as part of the form
    // // without any particular validation.
    // company: new FormControl(''),

    // // We don't need a special validator just for our app here, but there is a default one for email.
    // // We will require the email, though.
    // email: new FormControl('', Validators.compose([
    //   Validators.required,
    //   Validators.email,
    // ])),

  });


  // We can only display one error at a time,
  // the order the messages are defined in is the order they will display in.
  readonly addHuntValidationMessages = {
    title: [
      {type: 'required', message: 'Name is required'},
      {type: 'minlength', message: 'Name must be at least 2 characters long'},
      {type: 'maxlength', message: 'Name cannot be more than 50 characters long'},
      {type: 'existingName', message: 'Name has already been taken'}
    ],

    hostid: [
      {type: 'required', message: 'Name is required'},
      {type: 'minlength', message: 'Name must be at least 2 characters long'},
      {type: 'maxlength', message: 'Name cannot be more than 50 characters long'},
      {type: 'existingName', message: 'Name has already been taken'}
    ],

    description: [
      {type: 'required', message: 'Name is required'},
      {type: 'minlength', message: 'Name must be at least 2 characters long'},
      {type: 'maxlength', message: 'Name cannot be more than 50 characters long'},
      {type: 'existingName', message: 'Name has already been taken'}
    ],


  };

  constructor(
    private huntService: HuntService,
    private snackBar: MatSnackBar,
    private router: Router) {
  }

  formControlHasError(controlTitle: string): boolean {
    return this.addHuntForm.get(controlTitle).invalid &&
      (this.addHuntForm.get(controlTitle).dirty || this.addHuntForm.get(controlTitle).touched);
  }

  getErrorMessage(name: keyof typeof this.addHuntValidationMessages): string {
    for(const {type, message} of this.addHuntValidationMessages[name]) {
      if (this.addHuntForm.get(name).hasError(type)) {
        return message;
      }
    }
    return 'Unknown error';
  }

  submitForm() {
    this.huntService.addHunt(this.addHuntForm.value).subscribe({
      next: (newId) => {
        this.snackBar.open(
          `Added hunt ${this.addHuntForm.value.title}`,
          null,
          { duration: 2000 }
        );
        this.router.navigate(['/hunts/', newId]);
      },
      error: err => {
        this.snackBar.open(
          `Problem contacting the server – Error Code: ${err.status}\nMessage: ${err.message}`,
          'OK',
          { duration: 5000 }
        );
      },
    });
  }



}
