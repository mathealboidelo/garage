import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parking } from '../class/parking';

export interface RacerView {
  id: number;
  displayName: string;
  carName: string;
  carPower: number;
  carAspiration: string;
  carTireType: string;
  carGripModifier: number;
  defeated: boolean;
  isGang: boolean;
  isBoss: boolean;
  isSpecial: boolean;
  reputationRequired: number;
  gangName: string;
  prefix: string;
  specialCarId?: number;
}

export interface ParkingView {
  id: number;
  name: string;
  races: any[];
  racers: RacerView[];
  userReputation: number;
}

@Injectable({ providedIn: 'root' })
export class ParkingService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  findAll(): Observable<Parking[]> {
    return this.http.get<Parking[]>(`${this.base}/parkings`);
  }

  findById(id: number): Observable<Parking> {
    return this.http.get<Parking>(`${this.base}/parkings/${id}`);
  }

  getParkingForUser(parkingId: number, userId: number): Observable<ParkingView> {
    return this.http.get<ParkingView>(`${this.base}/parkings/${parkingId}/user/${userId}`);
  }
}
