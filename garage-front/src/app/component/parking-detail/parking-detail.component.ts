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

  // ── Race animation state ─────────────────────────────
  playerPos = 0;
  opponentPos = 0;
  raceFinished = false;
  playerFinishedFirst = false;
  private raceInterval: any;
  private apiResult: RaceResult | null = null;

  // Usure pneus en temps réel pendant la course
  liveTireWear    = 100;   // commence à 100%, descend pendant la course
  liveOilQuality  = 100;
  private tireWearPerTick = 0; // calculé depuis tireWearPerRace / nbTicks

  // Segment-based physics
  private segments: Array<{type: string, value: number}> = [];
  private playerSpeed   = 0;   // vitesse actuelle (unités/tick)
  private opponentSpeed = 0;
  private playerTarget  = 0;   // vitesse cible selon le segment courant
  private opponentTarget= 0;
  private readonly MAX_SPEED   = 1.2;  // vitesse max en ligne droite (px/%/tick)
  private readonly ACCEL        = 0.06; // accélération par tick
  private readonly DECEL        = 0.09; // décélération par tick (freinage)
  // Label segment courant pour affichage
  currentSegmentLabel = '';
  playerAhead = false;

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
    this.playerSpeed = 0; this.opponentSpeed = 0;
    this.playerTarget = 0; this.opponentTarget = 0;
    this.currentSegmentLabel = 'Départ...';
    this.playerAhead = false;
    // Usure temps réel : part du tireWear actuel de la voiture
    this.liveTireWear   = this.selectedCar?.tireWear   ?? 100;
    this.liveOilQuality = this.selectedCar?.oilQuality ?? 100;
    this.tireWearPerTick = 0; // sera calculé quand l'API répond

    // Parse segments pour la physique
    this.segments = this.generatedRace?.segments
      ? this.parseSegments(this.generatedRace.segments)
      : [];

    this.raceInterval = setInterval(() => this.animTick(), 50);

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

  /** Appelé à chaque tick (50ms) — moteur de simulation segment par segment */
  private animTick() {
    if (this.raceFinished) return;

    // ── Calcule la vitesse cible selon le segment courant ──
    // On détermine dans quel segment se trouve chaque voiture
    const pTarget = this.segmentTargetSpeed(this.playerPos,   this.apiResult, true);
    const oTarget = this.segmentTargetSpeed(this.opponentPos, this.apiResult, false);

    // ── Accélération/freinage progressif ──────────────────
    this.playerSpeed   = this.approach(this.playerSpeed,   pTarget, this.ACCEL, this.DECEL);
    this.opponentSpeed = this.approach(this.opponentSpeed, oTarget, this.ACCEL, this.DECEL);

    // ── Micro-bruit pour rendre la course imprévisible ─────
    const pNoise = 1 + (Math.random() - 0.5) * 0.12;
    const oNoise = 1 + (Math.random() - 0.5) * 0.12;

    this.playerPos   = Math.min(this.playerPos   + this.playerSpeed   * pNoise, 100);
    this.opponentPos = Math.min(this.opponentPos + this.opponentSpeed * oNoise, 100);

    // ── Mise à jour label segment ─────────────────────────
    this.currentSegmentLabel = this.getSegmentLabel(this.playerPos);
    this.playerAhead = this.playerPos >= this.opponentPos;

    // ── Usure pneus temps réel ───────────────────────────
    if (this.tireWearPerTick > 0) {
      this.liveTireWear   = Math.max(0, this.liveTireWear   - this.tireWearPerTick);
      this.liveOilQuality = Math.max(0, this.liveOilQuality - this.tireWearPerTick * 0.5);
    }

    // ── Fin de course ─────────────────────────────────────
    if (this.apiResult && (this.playerPos >= 92 || this.opponentPos >= 92)) {
      this.finishRace();
    }
  }

  /**
   * Calcule la vitesse cible pour une position donnée.
   * En droite → vitesse max pondérée par la perf de la voiture.
   * En virage → ralentissement proportionnel à l'angle (angle élevé = très lent).
   * Si apiResult est disponible, on scale sur le score réel.
   */
  private segmentTargetSpeed(pos: number, res: RaceResult | null, isPlayer: boolean): number {
    // Base speed from score ratio
    let baseSpeed = this.MAX_SPEED * 0.6; // avant que l'API réponde
    if (res) {
      const ps = res.playerScore, os = res.opponentScore;
      const myScore = isPlayer ? ps : os;
      const maxScore = Math.max(ps, os, 0.001);
      baseSpeed = this.MAX_SPEED * 0.4 + (myScore / maxScore) * this.MAX_SPEED * 0.6;
    }

    // Détermine le segment actuel (0-100% → quelle portion du circuit)
    if (this.segments.length === 0) return baseSpeed;

    // Longueur totale des segments
    const totalLen = this.segments.reduce((s, seg) =>
      s + (seg.type === 'S' ? seg.value : seg.value / 3), 0);
    const targetUnit = (pos / 100) * totalLen;

    let cursor = 0;
    for (const seg of this.segments) {
      const segLen = seg.type === 'S' ? seg.value : seg.value / 3;
      if (cursor + segLen >= targetUnit) {
        if (seg.type === 'S') {
          return baseSpeed; // pleine vitesse en droite
        } else {
          // Virage : réduction selon angle
          // 30°=0.85, 90°=0.55, 150°=0.25
          const factor = Math.max(0.20, 1.0 - (seg.value - 30) / 160);
          // Préfreinage : si le prochain segment est un virage, on commence à freiner avant
          return baseSpeed * factor;
        }
      }
      cursor += segLen;
    }
    return baseSpeed;
  }

  /** Approche douce d'une vitesse cible avec accél/décel différenciées */
  private approach(current: number, target: number, accel: number, decel: number): number {
    if (current < target) return Math.min(current + accel, target);
    if (current > target) return Math.max(current - decel, target);
    return current;
  }

  /** Label du segment courant pour l'affichage */
  private getSegmentLabel(pos: number): string {
    if (this.segments.length === 0) return '';
    const totalLen = this.segments.reduce((s, seg) =>
      s + (seg.type === 'S' ? seg.value : seg.value / 3), 0);
    const targetUnit = (pos / 100) * totalLen;
    let cursor = 0;
    for (const seg of this.segments) {
      const segLen = seg.type === 'S' ? seg.value : seg.value / 3;
      if (cursor + segLen >= targetUnit) {
        if (seg.type === 'S') return '⚡ Ligne droite';
        if (seg.value <= 45)  return '↗ Virage rapide ' + seg.value + '°';
        if (seg.value <= 90)  return '↪ Virage ' + seg.value + '°';
        return '🔴 Virage serré ' + seg.value + '°';
      }
      cursor += segLen;
    }
    return '';
  }

  private adjustSpeeds(res: RaceResult) {
    // Calcule l'usure pneus par tick (course dure ~100 ticks pour couvrir 92%)
    // L'API retourne tireWearPerRace (usure totale d'une course)
    const totalTicks = 92 / 0.6; // estimation: vitesse moy × ticks ≈ 92%
    this.tireWearPerTick = (res.tireWearPerRace ?? 8) / totalTicks;
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
      // Recharge le parking si gang, boss OU wanderer battu
      if ((this.apiResult!.gangMemberDefeated || this.apiResult!.bossDefeated
           || this.apiResult!.specialCarUnlocked) && this.user) {
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

  /** Parse segments string → array of {type, value} for display */
  parseSegments(segs: string | undefined): Array<{type: string, value: number}> {
    if (!segs) return [];
    return segs.split(',').map(s => {
      const [t, v] = s.trim().split(':');
      return { type: t, value: parseInt(v) };
    });
  }

  cornerLabel(angle: number): string {
    if (angle <= 45)  return 'Rapide';
    if (angle <= 90)  return 'Moyen';
    if (angle <= 120) return 'Serré';
    return 'Épingle';
  }

  cornerColor(angle: number): string {
    if (angle <= 45)  return 'cyan';
    if (angle <= 90)  return 'gold';
    return 'mag';
  }

  tireColor(v: number): string { return v > 60 ? 'cyan' : v > 30 ? 'gold' : 'mag'; }
  oilColor(v: number): string  { return v > 50 ? 'cyan' : v > 25 ? 'gold' : 'mag'; }
  powerPercent(p: number): number  { return Math.min((p / 600) * 100, 100); }
  weightPercent(w: number): number { return Math.max(0, 100 - ((w - 500) / 1500) * 100); }
  gripPercent(g: number): number   { return Math.min(((g - 1.0) / 0.6) * 100, 100); }
}
