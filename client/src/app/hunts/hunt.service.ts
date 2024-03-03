import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { Hunt } from './hunt';



@Injectable({
  providedIn: `root`
})
export class HuntService {
  // The URL for the hunts part of the server API.
  readonly huntUrl: string = `${environment.apiUrl}hunts`;

  private readonly hostKey = 'hostid';
  private readonly titleKey = 'title';
  private readonly descriptionKey = 'description';
  private readonly taskKey = 'task';

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
  getHunts(filters?: { hostid?: string; title?: string; task: string; description: string}): Observable<Hunt[]> {
    let httpParams: HttpParams = new HttpParams();
    if (filters) {
      if (filters.hostid) {
        httpParams = httpParams.set(this.hostKey, filters.hostid);
      }
      if (filters.title) {
        httpParams = httpParams.set(this.titleKey, filters.title);
      }

      if (filters.description) {
        httpParams = httpParams.set(this.descriptionKey, filters.description);
      }

      if (filters.task) {
        httpParams = httpParams.set(this.taskKey, filters.task);
      }

    }

    return this.httpClient.get<Hunt[]>(this.huntUrl, {
      params: httpParams
    });
  }

  getHuntById(id: string): Observable<Hunt> {
    // The input to get could also be written as (this.userUrl + '/' + id)
    return this.httpClient.get<Hunt>(`${this.huntUrl}/${id}`);
  }

  addHunt(newHunt: Partial<Hunt>): Observable<string> {
    // Send post request to add a new user with the user data as the body.
    return this.httpClient.post<{id: string}>(this.huntUrl, newHunt).pipe(map(res => res.id));
  }
}

