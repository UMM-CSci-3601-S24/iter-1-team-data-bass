import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Task } from './task';


@Injectable({
  providedIn: `root`
})
export class TaskService {
  // The URL for the hunts part of the server API.
  readonly huntUrl: string = `${environment.apiUrl}hunts`;

  private readonly huntIdKey = 'huntId';
  private readonly taskDescriptionKey = 'taskDescription';


  constructor(private httpClient: HttpClient) {
  }

  /**
  *  @param filters a map that allows us to specify a target hostid
  *  @returns an `Observable` of an array of `Hunts`. Wrapping the array
  *   in an `Observable` means that other bits of of code can `subscribe` to
  *   the result (the `Observable`) and get the results that come back
  *   from the server after a possibly substantial delay (because we're
  *   contacting a remote server over the Internet).
  */
  getTasks(filters?: { huntId: string; taskDescription?: string}): Observable<Task[]> {
    let httpParams: HttpParams = new HttpParams();
    if (filters) {
      if (filters.huntId) {
        httpParams = httpParams.set(this.huntIdKey, filters.huntId);
      }

      if (filters.taskDescription) {
        httpParams = httpParams.set(this.taskDescriptionKey, filters.taskDescription);
      }


    }

    return this.httpClient.get<Task[]>(this.huntUrl, {
      params: httpParams
    });
  }

  getTaskById(id: string): Observable<Task> {
    // The input to get could also be written as (this.userUrl + '/' + id)
    return this.httpClient.get<Task>(`${this.huntUrl}/${id}`);
  }

  addTask(newTask: Partial<Task>): Observable<string> {
    // Send post request to add a new user with the user data as the body.
    return this.httpClient.post<{id: string}>(this.huntUrl, newTask).pipe(map(res => res.id));
  }
}
