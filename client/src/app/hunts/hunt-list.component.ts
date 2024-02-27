import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';
import { Hunt } from './hunt';
import { HuntService } from './hunt.service';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { UserCardComponent } from '../users/user-card.component';
import { HuntCardComponent } from "./hunt-card.component";

@Component({
    selector: 'app-hunt-list',
    standalone: true,
    templateUrl: './hunt-list.component.html',
    styleUrl: './hunt-list.component.scss',
    imports: [HuntCardComponent, MatCardModule, MatFormFieldModule, MatInputModule, FormsModule, MatSelectModule, MatOptionModule, MatRadioModule, UserCardComponent, MatListModule, RouterLink, MatButtonModule, MatTooltipModule, MatIconModule, HuntCardComponent]
})
export class HuntListComponent implements OnInit, OnDestroy {
  // These are public so that tests can reference them (.spec.ts)
  public serverFilteredHunts: Hunt[];

  public huntHost: string;

  errMsg = '';
  private ngUnsubscribe = new Subject<void>();

  /**
   * This constructor injects both an instance of `HuntService`
   * and an instance of `MatSnackBar` into this component.
   * `HuntService` lets us interact with the server.
   *
   * @param huntService the `HuntService` used to get hunts from the server
   * @param snackBar the `MatSnackBar` used to display feedback
   */
  constructor(private huntService: HuntService, private snackBar: MatSnackBar) {
    // Nothing here – everything is in the injection parameters.
  }

  /**
   * Get the hunts from the server
   */
  getHuntsFromServer(): void {
    this.huntService.getHunts({
      hostid: this.huntHost,

    }).pipe(
      takeUntil(this.ngUnsubscribe)
    ).subscribe({
      next: (returnedHunts) => {
        this.serverFilteredHunts = returnedHunts;
      },
      error: (err) => {
        if (err.error instanceof ErrorEvent) {
          // A client-side or network error occurred. Handle it accordingly.
          this.errMsg = `Problem in the client - Error: ${err.error.message}`;
        } else {
          this.errMsg = `Problem on the server - Error Code: ${err.status}\nMessage: ${err.message}`;
        }
        this.snackBar.open(
          this.errMsg,
          'OK',
          { duration: 6000 });
      },
    });
  }

  ngOnInit(): void {
    this.getHuntsFromServer();
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

}
