import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface TeamInvitation {
  id: number; name: string; rank: number;
  carName: string; carPower: number; carWeight: number;
  carGrip: number; carAspiration: string; teamColor: string;
  budget: number; description: string; reputationRequired: number;
  estimatedSeasonEarnings: number;
}

export interface DriverResult {
  driverName: string; teamName: string; teamColor: string;
  carName: string; carPower: number;
  avgScore: number; lapScores: number[]; isPlayer: boolean;
}

export interface CarStats {
  carName: string; power: number; weight: number; grip: number;
  aspiration: string; teamColor: string;
  engineBonus: number; gripBonus: number; weightBonus: number;
  finalTireWear: number; finalOilQuality: number;
  lapScores: number[];
}

export interface UpgradeOption {
  type: string; name: string; description: string; cost: number; canAfford: boolean;
}

export interface UpgradeMenu {
  teamName: string; teamColor: string; budget: number;
  power: number; grip: number; weight: number;
  options: UpgradeOption[];
}

export interface SrcRaceResult {
  raceName: string; straightLine: number; corner: number;
  results: DriverResult[]; playerPosition: number; pointsEarned: number;
  raceTeamEarnings: number; totalTeamEarnings: number;
  totalPoints: number; raceNumber: number;
  seasonOver: boolean; playerPayout: number; newBalance: number;
  carStats: CarStats;
}

export interface SeasonState {
  seasonId: number; teamName: string; teamColor: string;
  carName: string; carPower: number; carWeight: number; carGrip: number;
  engineBonus: number; gripBonus: number; weightBonus: number;
  currentRace: number; totalRaces: number; playerPoints: number;
  finished: boolean; playerEarnings: number;
  racePositions: number[]; teamBudget: number; teamSeasonEarnings: number;
}

@Injectable({ providedIn: 'root' })
export class SrcService {
  private base = 'http://localhost:8080/api/src';
  constructor(private http: HttpClient) {}

  getInvitations(userId: number): Observable<TeamInvitation[]> {
    return this.http.get<TeamInvitation[]>(`${this.base}/invitations/${userId}`);
  }
  joinTeam(userId: number, teamId: number): Observable<SeasonState> {
    return this.http.post<SeasonState>(`${this.base}/join`, { userId, teamId });
  }
  getSeason(userId: number): Observable<SeasonState | null> {
    return this.http.get<SeasonState | null>(`${this.base}/season/${userId}`);
  }
  runRace(userId: number): Observable<SrcRaceResult> {
    return this.http.post<SrcRaceResult>(`${this.base}/race/run`, { userId });
  }
  getUpgrades(userId: number): Observable<UpgradeMenu> {
    return this.http.get<UpgradeMenu>(`${this.base}/upgrades/${userId}`);
  }
  applyUpgrade(userId: number, type: string): Observable<string> {
    return this.http.post(`${this.base}/upgrade`, { userId, type }, { responseType: 'text' });
  }
}