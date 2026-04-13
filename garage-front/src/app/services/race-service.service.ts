import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RaceResult } from '../class/race-result';

export interface RaceRequest {
  userId: number;
  opponentId: number;
  raceId: number;
  bet: number;
}

@Injectable({ providedIn: 'root' })
export class RaceService {

  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  runRace(req: RaceRequest): Observable<RaceResult> {
    return this.http.post<RaceResult>(`${this.base}/race/run`, req);
  }
}