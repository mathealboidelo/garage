import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import {
  SrcService, TeamInvitation, SeasonState, SrcRaceResult,
  DriverResult, UpgradeMenu
} from 'src/app/services/src.service';
import { User } from 'src/app/class/user';

type SrcPhase = 'invitations' | 'season' | 'racing' | 'race-result' | 'car-stats' | 'upgrades' | 'season-over';

@Component({
  selector: 'app-src-championship',
  templateUrl: './src-championship.component.html',
  styleUrls: ['./src-championship.component.css']
})
export class SrcChampionshipComponent implements OnInit, OnDestroy {

  user: User | null = null;
  phase: SrcPhase = 'invitations';
  loading = true;

  invitations: TeamInvitation[] = [];
  season: SeasonState | null = null;
  raceResult: SrcRaceResult | null = null;
  upgradeMenu: UpgradeMenu | null = null;
  upgradeMsg = '';

  // ── Race animation state ──────────────────────────────
  // 20 pilotes avec positions indépendantes (0-100%)
  driverPositions: number[] = [];
  driverSpeeds: number[] = [];
  currentLap = 0;         // 0, 1, 2
  lapTireWear = 100;
  lapOilQuality = 100;
  private animInterval: any;
  private apiResult: SrcRaceResult | null = null;
  private raceFinished = false;

  constructor(
    private userService: UserService,
    private srcService: SrcService,
    private router: Router
  ) {}

  ngOnInit() {
    const s = this.userService.getSession();
    if (!s) { this.router.navigate(['/']); return; }
    this.userService.findById(s.id).subscribe(u => {
      this.user = u;
      this.loading = false;
      this.srcService.getSeason(u.id).subscribe(season => {
        if (season) { this.season = season; this.phase = season.finished ? 'season-over' : 'season'; }
        else this.loadInvitations();
      });
    });
  }

  ngOnDestroy() { this.clearAnim(); }

  loadInvitations() {
    if (!this.user) return;
    this.srcService.getInvitations(this.user.id).subscribe(inv => {
      this.invitations = inv; this.phase = 'invitations';
    });
  }

  joinTeam(team: TeamInvitation) {
    if (!this.user) return;
    if (!confirm(`Rejoindre ${team.name} pour une saison de 10 courses ?`)) return;
    this.srcService.joinTeam(this.user.id, team.id).subscribe(season => {
      this.season = season; this.phase = 'season';
    });
  }

  // ── Race ─────────────────────────────────────────────

  startRace() {
    if (!this.user || !this.season) return;
    this.phase = 'racing';
    this.raceFinished = false;
    this.apiResult = null;
    this.currentLap = 0;
    this.lapTireWear = 100;
    this.lapOilQuality = 100;

    // Init 20 positions — joueur en position 0
    this.driverPositions = Array(20).fill(0).map((_, i) => -i * 3); // grille de départ décalée
    // Vitesses initiales aléatoires (joueur = index 0, légèrement favorisé visuellement)
    this.driverSpeeds = Array(20).fill(0).map((_, i) =>
      i === 0 ? 0.55 + Math.random() * 0.15 : 0.40 + Math.random() * 0.25
    );

    this.animInterval = setInterval(() => this.animStep(), 50);

    // Appel API
    this.srcService.runRace(this.user.id).subscribe({
      next: res => { this.apiResult = res; this.adjustSpeedsFromResult(res); },
      error: err => { alert('Erreur: ' + (err.error || 'inconnue')); this.phase = 'season'; this.clearAnim(); }
    });
  }

  private animStep() {
    if (this.raceFinished) return;

    // Avance chaque pilote
    for (let i = 0; i < 20; i++) {
      const noiseSpeed = this.driverSpeeds[i] * (0.9 + Math.random() * 0.2);
      this.driverPositions[i] = Math.min(this.driverPositions[i] + noiseSpeed, 100);
    }

    // Tour suivant : le joueur (idx 0) a bouclé
    if (this.driverPositions[0] >= 100 && this.currentLap < 2) {
      this.currentLap++;
      this.lapTireWear   = Math.max(0, this.lapTireWear   - 12);
      this.lapOilQuality = Math.max(0, this.lapOilQuality - 8);
      // Repart de 0 pour simuler le nouveau tour
      for (let i = 0; i < 20; i++) this.driverPositions[i] = 0;
    }

    // Fin de course : 3 tours bouclés ET API revenue
    if (this.driverPositions[0] >= 100 && this.currentLap >= 2 && this.apiResult) {
      this.finishRace();
    }
  }

  private adjustSpeedsFromResult(res: SrcRaceResult) {
    // Recalcule les vitesses pour que l'ordre final reflète le résultat
    const sorted = [...res.results].sort((a, b) => b.avgScore - a.avgScore);
    const maxScore = sorted[0]?.avgScore ?? 1;
    // Joueur
    const playerScore = res.results.find(r => r.isPlayer)?.avgScore ?? maxScore * 0.5;
    this.driverSpeeds[0] = 0.4 + (playerScore / maxScore) * 0.35;
    // IA
    for (let i = 1; i < 20; i++) {
      const aiScore = sorted[i]?.avgScore ?? maxScore * 0.5;
      this.driverSpeeds[i] = 0.4 + (aiScore / maxScore) * 0.35;
    }
  }

  private finishRace() {
    this.raceFinished = true;
    this.clearAnim();
    setTimeout(() => {
      this.raceResult = this.apiResult;
      this.phase = 'race-result';
      if (this.user && this.apiResult) this.user.credits = this.apiResult.newBalance;
      this.srcService.getSeason(this.user!.id).subscribe(s => this.season = s);
    }, 800);
  }

  private clearAnim() {
    if (this.animInterval) { clearInterval(this.animInterval); this.animInterval = null; }
  }

  // ── Navigation post-course ────────────────────────────

  goToCarStats() { this.phase = 'car-stats'; }

  goToUpgrades() {
    if (!this.user) return;
    this.srcService.getUpgrades(this.user.id).subscribe(menu => {
      this.upgradeMenu = menu; this.phase = 'upgrades';
    });
  }

  applyUpgrade(type: string) {
    if (!this.user) return;
    this.srcService.applyUpgrade(this.user.id, type).subscribe({
      next: msg => {
        this.upgradeMsg = msg;
        this.srcService.getUpgrades(this.user!.id).subscribe(m => this.upgradeMenu = m);
        this.srcService.getSeason(this.user!.id).subscribe(s => this.season = s);
        setTimeout(() => this.upgradeMsg = '', 3000);
      },
      error: e => { this.upgradeMsg = e.error; setTimeout(() => this.upgradeMsg = '', 3000); }
    });
  }

  continueAfterRace() { this.phase = 'race-result'; }
  backToSeason()      { this.phase = 'season'; this.raceResult = null; }
  startNewSeason()    { this.season = null; this.raceResult = null; this.loadInvitations(); }
  goBack()            { this.router.navigate(['/dashboard']); }

  // ── Display helpers ───────────────────────────────────

  get sortedResults(): DriverResult[] { return this.raceResult?.results ?? []; }
  get playerResult(): DriverResult | undefined { return this.raceResult?.results.find(r => r.isPlayer); }
  get playerPos(): number { return this.driverPositions[0] ?? 0; }

  /** Position du joueur dans le classement en cours (basé sur les positions des pilotes) */
  get liveRanking(): number {
    const playerPos = this.driverPositions[0] ?? 0;
    return this.driverPositions.filter(p => p > playerPos).length + 1;
  }

  rankLabel(rank: number): string {
    return ['','S+','S','A+','A','B+','B','C+','C','D+','D'][rank] ?? 'D';
  }
  rankColor(rank: number): string {
    if (rank === 1) return '#ff003c';
    if (rank <= 3)  return '#ff8c00';
    if (rank <= 6)  return '#ffd700';
    if (rank <= 8)  return '#00f0ff';
    return '#888';
  }
  posLabel(pos: number): string {
    if (pos === 1) return '🥇'; if (pos === 2) return '🥈'; if (pos === 3) return '🥉';
    return pos + 'e';
  }
  pointsForPos(pos: number): number {
    return [0,25,18,15,12,10,8,6,4,2,1][pos] ?? 0;
  }
  get progressPercent(): number {
    return this.season ? (this.season.currentRace / this.season.totalRaces) * 100 : 0;
  }
  tireColor(v: number): string { return v > 60 ? 'cyan' : v > 30 ? 'gold' : 'mag'; }
  oilColor(v: number): string  { return v > 50 ? 'cyan' : v > 25 ? 'gold' : 'mag'; }
  powerPercent(p: number): number  { return Math.min((p / 600) * 100, 100); }
  weightPercent(w: number): number { return Math.max(0, 100 - ((w - 900) / 3)); }
  gripPercent(g: number): number   { return Math.min(((g - 1.2) / 0.7) * 100, 100); }
}
