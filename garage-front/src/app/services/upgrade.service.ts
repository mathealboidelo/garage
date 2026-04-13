import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CarUpgrade, UpgradeResult } from '../class/car-upgrade';

@Injectable({ providedIn: 'root' })
export class UpgradeService {

  private base = 'http://localhost:8080/api/upgrade';

  constructor(private http: HttpClient) {}

  getUpgrades(carId: number): Observable<CarUpgrade> {
    return this.http.get<CarUpgrade>(`${this.base}/car/${carId}`);
  }

  buyUpgrade(userId: number, carId: number, upgradeType: string): Observable<UpgradeResult> {
    return this.http.post<UpgradeResult>(`${this.base}/buy`, { userId, carId, upgradeType });
  }
}
