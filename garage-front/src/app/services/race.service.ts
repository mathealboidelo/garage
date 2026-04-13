import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RaceResult } from '../class/race-result';
import { GeneratedRace } from '../class/generatedrace';

export interface RaceRequest {
  userId: number;
  carId: number;
  opponentId: number;
  raceId: number;
  bet: number;
  betCar: boolean;
  wagerCarId: number;
}

@Injectable({ providedIn: 'root' })
export class RaceService {
  private base = 'http://localhost:8080/api';
  constructor(private http: HttpClient) {}

  /** Génère une course aléatoire avec difficulté/multiplicateur */
  generateRace(opponentId: number, carId: number, userId: number): Observable<GeneratedRace> {
    return this.http.get<GeneratedRace>(
      `${this.base}/race/generate?opponentId=${opponentId}&carId=${carId}&userId=${userId}`
    );
  }

  runRace(req: RaceRequest): Observable<RaceResult> {
    return this.http.post<RaceResult>(`${this.base}/race/run`, req);
  }
}
