import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CarUpgrade, UpgradeResult } from '../class/car-upgrade';

export interface TireCatalogEntry {
  model: string;
  label: string;
  category: string;       // Street | Sport | Racing
  gripBonus: number;
  wearRateMultiplier: number;
  upgradeLevel: number;
  buyCost: number;
  changeCost: number;
  description: string;
}

@Injectable({ providedIn: 'root' })
export class UpgradeService {
  private base = 'http://localhost:8080/api/upgrade';
  constructor(private http: HttpClient) {}

  getUpgrades(carId: number): Observable<CarUpgrade> {
    console.log('[UPGRADE-SERVICE] getUpgrades carId=', carId);
    return this.http.get<CarUpgrade>(`${this.base}/car/${carId}`);
  }

  buyUpgrade(userId: number, carId: number, upgradeType: string): Observable<UpgradeResult> {
    return this.http.post<UpgradeResult>(`${this.base}/buy`, { userId, carId, upgradeType });
  }

  getTiresCatalog(carId: number): Observable<TireCatalogEntry[]> {
    console.log('[UPGRADE-SERVICE] getTiresCatalog carId=', carId);
    return this.http.get<TireCatalogEntry[]>(`${this.base}/tires/catalog/${carId}`);
  }

  buyTires(userId: number, carId: number, tireModel: string): Observable<UpgradeResult> {
    console.log('[UPGRADE-SERVICE] buyTires', { userId, carId, tireModel });
    return this.http.post<UpgradeResult>(`${this.base}/tires/buy`, { userId, carId, tireModel });
  }
}
