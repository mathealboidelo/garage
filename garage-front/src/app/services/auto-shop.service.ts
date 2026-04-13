import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ServiceResult {
  message: string;
  cost: number;
  newBalance: number;
  newTireWear: number;
  newOilQuality: number;
}

export interface CostInfo {
  tireCost: number;
  oilCost: number;
  saleValue: number;
}

@Injectable({ providedIn: 'root' })
export class AutoShopService {
  private base = 'http://localhost:8080/api/autoshop';
  constructor(private http: HttpClient) {}

  getCosts(carId: number): Observable<CostInfo> {
    return this.http.get<CostInfo>(`${this.base}/costs/${carId}`);
  }
  changeTires(carId: number, userId: number): Observable<ServiceResult> {
    return this.http.post<ServiceResult>(`${this.base}/tires/${carId}/user/${userId}`, {});
  }
  changeOil(carId: number, userId: number): Observable<ServiceResult> {
    return this.http.post<ServiceResult>(`${this.base}/oil/${carId}/user/${userId}`, {});
  }
  sellCar(carId: number, userId: number): Observable<ServiceResult> {
    return this.http.post<ServiceResult>(`${this.base}/sell/${carId}/user/${userId}`, {});
  }
}
