import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Car } from 'src/app/class/car';
import { RaceResult } from 'src/app/class/race-result';
import { GeneratedRace } from 'src/app/class/generatedrace';
import { User } from 'src/app/class/user';
import { ParkingService, ParkingView, RacerView } from 'src/app/services/parking.service';
import { UserService } from 'src/app/services/user.service';
import { RaceService } from 'src/app/services/race.service';

type RacePhase = 'lobby' | 'car-select' | 'challenge' | 'racing' | 'result';

@Component({
  selector: 'app-parking-detail',
  templateUrl: './parking-detail.component.html',
  styleUrls: ['./parking-detail.component.css']
})
export class ParkingDetailComponent implements OnInit, OnDestroy {

  parkingView: ParkingView | null = null;
  user: User | null = null;
  loading = true;

  phase: RacePhase = 'lobby';
  selectedOpponent: RacerView | null = null;
  selectedCar: Car | null = null;
  generatedRace: GeneratedRace | null = null;
  loadingRace = false;

  // Bet
  bet = 1000;
  betError = '';
  betCar = false;

  // Race animation
  playerPos = 0;
  opponentPos = 0;
  raceFinished = false;
  playerFinishedFirst = false;
  private playerSpeed = 0;
  private opponentSpeed = 0;
  private raceInterval: any;
  private apiResult: RaceResult | null = null;

  // Result
  result: RaceResult | null = null;
  playerBarWidth = 0;
  opponentBarWidth = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private parkingService: ParkingService,
    private userService: UserService,
    private raceService: RaceService
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    const session = this.userService.getSession();
    if (!session) { this.router.navigate(['/']); return; }

    this.userService.findById(session.id).subscribe(u => {
      this.user = u;
      if (u.garage?.cars?.length > 0) this.selectedCar = u.garage.cars[0];

      if (id) {
        this.parkingService.getParkingForUser(id, u.id).subscribe({
          next: pv => { this.parkingView = pv; this.loading = false; },
          error: () => this.loading = false
        });
      }
    });
  }

  ngOnDestroy() { this.clearRaceInterval(); }

  // ── Gang helpers ──────────────────────────────────────
  get gangMembers(): RacerView[] {
    return this.parkingView?.racers.filter(r => r.isGang && !r.isBoss) ?? [];
  }
  get boss(): RacerView | null {
    return this.parkingView?.racers.find(r => r.isBoss) ?? null;
  }
  get randomRacers(): RacerView[] {
    return this.parkingView?.racers.filter(r => !r.isGang && !r.isSpecial) ?? [];
  }
  get specialRacers(): RacerView[] {
    return this.parkingView?.racers.filter(r => r.isSpecial) ?? [];
  }
  get gangDefeatedCount(): number { return this.gangMembers.filter(r => r.defeated).length; }
  get allGangDefeated(): boolean  { return this.gangMembers.length > 0 && this.gangMembers.every(r => r.defeated); }
  get gangName(): string          { return this.gangMembers[0]?.gangName ?? ''; }

  // ── Challenge flow ────────────────────────────────────
  openCarSelect(opponent: RacerView) {
    if (opponent.isBoss && !this.allGangDefeated) return;
    this.selectedOpponent = opponent;
    this.bet = 1000; this.betError = ''; this.betCar = false;
    this.generatedRace = null;
    const cars = this.user?.garage?.cars ?? [];
    if (cars.length <= 1) {
      this.selectedCar = cars[0] ?? null;
      this.phase = 'challenge';
      this.generateRace();
    } else {
      this.phase = 'car-select';
    }
  }

  pickCar(car: Car) { this.selectedCar = car; }

  confirmCarAndChallenge() {
    this.phase = 'challenge';
    this.generateRace();
  }

  /** Appelle le backend pour générer une course aléatoire */
  generateRace() {
    if (!this.selectedCar || !this.selectedOpponent || !this.user) return;
    this.loadingRace = true;
    this.generatedRace = null;
    this.raceService.generateRace(
      this.selectedOpponent.id,
      this.selectedCar.id,
      this.user.id
    ).subscribe({
      next: gr => {
        this.generatedRace = gr;
        this.loadingRace = false;
        // Recadre la mise si elle dépasse le nouveau plafond
        if (this.bet > gr.maxBet) this.bet = gr.maxBet;
      },
      error: () => this.loadingRace = false
    });
  }

  /** Retirer une nouvelle course (re-roll) */
  rerollRace() { this.generateRace(); }

  toggleCarBet() { this.betCar = !this.betCar; }

  confirmRace() {
    if (!this.user || !this.selectedCar || !this.selectedOpponent || !this.generatedRace) return;

    if (!this.betCar) {
      const maxBet = Math.min(this.user.credits, 50000);
      if (this.bet < 100)    { this.betError = 'Mise minimum : 100 CR'; return; }
      if (this.bet > maxBet) { this.betError = `Maximum : ${maxBet.toLocaleString()} CR`; return; }
    }

    this.betError = '';
    this.phase = 'racing';
    this.playerPos = 0; this.opponentPos = 0;
    this.raceFinished = false; this.apiResult = null;

    this.playerSpeed   = 0.45 + Math.random() * 0.15;
    this.opponentSpeed = 0.45 + Math.random() * 0.15;

    this.raceInterval = setInterval(() => {
      if (this.raceFinished) return;
      this.playerPos   = Math.min(this.playerPos   + this.playerSpeed,   100);
      this.opponentPos = Math.min(this.opponentPos + this.opponentSpeed, 100);
      if (this.apiResult && (this.playerPos >= 92 || this.opponentPos >= 92)) {
        this.finishRace();
      }
    }, 50);

    this.raceService.runRace({
      userId:     this.user.id,
      carId:      this.selectedCar.id,
      opponentId: this.selectedOpponent.id,
      raceId:     this.generatedRace.raceId,
      bet:        this.betCar ? 0 : this.bet,
      betCar:     this.betCar,
      wagerCarId: this.betCar ? this.selectedCar.id : 0
    }).subscribe({
      next: res => { this.apiResult = res; this.adjustSpeeds(res); },
      error: err => { alert(err.error || 'Erreur'); this.phase = 'challenge'; this.clearRaceInterval(); }
    });
  }

  private adjustSpeeds(res: RaceResult) {
    const ps = res.playerScore, os = res.opponentScore, total = ps + os;
    this.playerSpeed   = 0.8 * (ps / total) * 2;
    this.opponentSpeed = 0.8 * (os / total) * 2;
  }

  private finishRace() {
    if (this.raceFinished || !this.apiResult) return;
    this.raceFinished = true;
    this.clearRaceInterval();
    const won = this.apiResult.playerWon;
    this.playerPos = won ? 100 : 96;
    this.opponentPos = won ? 96 : 100;
    this.playerFinishedFirst = won;

    setTimeout(() => {
      this.result = this.apiResult;
      this.animateBars(this.apiResult!);
      this.phase = 'result';
      if (this.user) {
        this.user.credits    = this.apiResult!.newBalance;
        this.user.reputation = this.apiResult!.newReputation;
      }
      if (this.selectedCar && this.apiResult) {
        this.selectedCar.tireWear   = this.apiResult.newTireWear;
        this.selectedCar.oilQuality = this.apiResult.newOilQuality;
      }
      if ((this.apiResult!.gangMemberDefeated || this.apiResult!.bossDefeated) && this.user) {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        this.parkingService.getParkingForUser(id, this.user.id).subscribe(pv => this.parkingView = pv);
      }
    }, 1000);
  }

  private clearRaceInterval() {
    if (this.raceInterval) { clearInterval(this.raceInterval); this.raceInterval = null; }
  }

  private animateBars(res: RaceResult) {
    const max = Math.max(res.playerScore, res.opponentScore);
    const tP = (res.playerScore / max) * 100;
    const tO = (res.opponentScore / max) * 100;
    let step = 0;
    const iv = setInterval(() => {
      step += 2;
      this.playerBarWidth   = Math.min((step / 100) * tP, tP);
      this.opponentBarWidth = Math.min((step / 100) * tO, tO);
      if (step >= 100) clearInterval(iv);
    }, 20);
  }

  rematch() {
    this.result = null; this.playerBarWidth = 0; this.opponentBarWidth = 0;
    this.clearRaceInterval(); this.phase = 'challenge';
    this.generateRace();
  }

  backToLobby() {
    this.result = null; this.selectedOpponent = null;
    this.selectedCar = this.user?.garage?.cars?.[0] ?? null;
    this.playerBarWidth = 0; this.opponentBarWidth = 0;
    this.betCar = false; this.generatedRace = null;
    this.clearRaceInterval(); this.phase = 'lobby';
  }

  goBack() { this.router.navigate(['/dashboard']); }

  // ── Display helpers ───────────────────────────────────
  get maxBet(): number {
    if (!this.user) return 0;
    // Utilise le plafond calculé par le serveur si disponible
    return this.generatedRace?.maxBet ?? Math.min(this.user.credits, 50000);
  }
  get opponentPower(): number { return this.selectedOpponent?.carPower ?? 0; }

  difficultyColor(d: number): string {
    if (d <= 2) return 'easy';
    if (d <= 4) return 'medium';
    if (d <= 6) return 'hard';
    if (d <= 8) return 'vhard';
    return 'extreme';
  }

  expectedGain(bet: number, mult: number): number {
    return Math.floor(bet * mult);
  }

  carRatingLabel(power: number | undefined): string {
    if (!power) return 'D';
    if (power >= 400) return 'S';
    if (power >= 300) return 'A';
    if (power >= 200) return 'B';
    if (power >= 150) return 'C';
    return 'D';
  }

  tireColor(v: number): string { return v > 60 ? 'cyan' : v > 30 ? 'gold' : 'mag'; }
  oilColor(v: number): string  { return v > 50 ? 'cyan' : v > 25 ? 'gold' : 'mag'; }
  powerPercent(p: number): number  { return Math.min((p / 600) * 100, 100); }
  weightPercent(w: number): number { return Math.max(0, 100 - ((w - 500) / 1500) * 100); }
  gripPercent(g: number): number   { return Math.min(((g - 1.0) / 0.6) * 100, 100); }
}
