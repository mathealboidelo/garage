import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Car } from 'src/app/class/car';
import { CarUpgrade, UpgradeCategory, UpgradeResult } from 'src/app/class/car-upgrade';
import { User } from 'src/app/class/user';
import { UpgradeService, TireCatalogEntry } from 'src/app/services/upgrade.service';
import { UserService } from 'src/app/services/user.service';

export interface UpgradeDef {
  type: UpgradeCategory;
  label: string;
  icon: string;
  description: string;
  statAffected: string;
  levelKey: keyof CarUpgrade;
}

@Component({
  selector: 'app-garage-upgrade',
  templateUrl: './garage-upgrade.component.html',
  styleUrls: ['./garage-upgrade.component.css']
})
export class GarageUpgradeComponent implements OnInit {

  user: User | null = null;
  selectedCar: Car | null = null;
  upgrades: CarUpgrade | null = null;
  loading = true;
  buying: UpgradeCategory | string | null = null;
  lastResult: UpgradeResult | null = null;
  resultTimeout: any;

  readonly Math = Math;  // exposé pour le template

  // Pneus
  tiresCatalog: TireCatalogEntry[] = [];
  tiresTab: 'street' | 'sport' | 'racing' = 'street';
  loadingTires = false;

  readonly catalog: UpgradeDef[] = [
    { type: 'ENGINE',       label: 'Moteur',       icon: '⚙',  description: 'Augmente la puissance. Idéal pour les circuits rapides.',          statAffected: 'power',  levelKey: 'engineLevel' },
    { type: 'TRANSMISSION', label: 'Transmission', icon: '⚡', description: 'Améliore le couple. Gain de puissance + léger gain de grip.',        statAffected: 'power',  levelKey: 'transmissionLevel' },
    { type: 'SUSPENSION',   label: 'Suspension',   icon: '🔧', description: 'Meilleure tenue en virage. Essentiel pour le touge.',               statAffected: 'grip',   levelKey: 'suspensionLevel' },
    { type: 'BRAKES',       label: 'Freins',       icon: '🛑', description: 'Freinage amélioré = meilleure sortie de virage.',                   statAffected: 'grip',   levelKey: 'brakesLevel' },
    { type: 'WEIGHT',       label: 'Allègement',   icon: '⬇',  description: 'Carbone et titane. Moins de poids = plus de vivacité.',             statAffected: 'weight', levelKey: 'weightLevel' },
  ];

  constructor(
    private userService: UserService,
    private upgradeService: UpgradeService,
    public router: Router
  ) {}

  ngOnInit() {
    const session = this.userService.getSession();
    if (!session) { this.router.navigate(['/']); return; }
    this.userService.findById(session.id).subscribe({
      next: u => {
        this.user = u;
        if (u.garage?.cars?.length > 0) this.selectCar(u.garage.cars[0]);
        else this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  selectCar(car: Car) {
    this.selectedCar = car;
    this.upgrades = null;
    this.loading = true;
    this.tiresCatalog = [];
    this.upgradeService.getUpgrades(car.id).subscribe({
      next: u => { this.upgrades = u; this.loading = false; this.loadTiresCatalog(); },
      error: () => { this.loading = false; }
    });
  }

  loadTiresCatalog() {
    if (!this.selectedCar) return;
    console.log('[UPGRADE-COMPONENT] loadTiresCatalog pour carId=', this.selectedCar.id);
    this.loadingTires = true;
    this.upgradeService.getTiresCatalog(this.selectedCar.id).subscribe({
      next: c => {
        console.log('[UPGRADE-COMPONENT] catalogue recu:', c?.length, 'entrees', c);
        if (c && c.length > 0) {
          this.tiresCatalog = c;
        } else {
          console.warn('[UPGRADE-COMPONENT] catalogue vide -> fallback');
          this.tiresCatalog = this.buildFallbackCatalog();
        }
        this.loadingTires = false;
      },
      error: (err) => {
        console.error('[UPGRADE-COMPONENT] erreur catalogue:', err.status, err.error);
        this.tiresCatalog = this.buildFallbackCatalog();
        this.loadingTires = false;
      }
    });
  }

  /** Catalogue de secours avec prix calculés côté front */
  private buildFallbackCatalog(): TireCatalogEntry[] {
    const p = Math.max(this.selectedCar?.price ?? 5000, 5000);
    const m = p < 15000 ? 1.0 : p < 50000 ? 1.3 : p < 120000 ? 1.6 : 2.0;
    return [
      // Rue (3)
      { model: 'Street_Soft', label: 'Rue Tendre',    category: 'Street', gripBonus:  0.03, wearRateMultiplier: 1.1,  upgradeLevel: 0, buyCost: Math.round(1200 * m), changeCost: Math.round(400 * m),  description: "Pneus route tendres. Bon grip par temps froid." },
      { model: 'Street',      label: 'Rue Normal',    category: 'Street', gripBonus:  0.00, wearRateMultiplier: 1.0,  upgradeLevel: 0, buyCost: Math.round(1500 * m), changeCost: Math.round(500 * m),  description: "Pneus de serie. Bon compromis confort/durabilite." },
      { model: 'Street_Hard', label: 'Rue Dur',       category: 'Street', gripBonus: -0.02, wearRateMultiplier: 0.8,  upgradeLevel: 0, buyCost: Math.round(1000 * m), changeCost: Math.round(350 * m),  description: "Pneus route durs. Longue duree de vie." },
      // Sport (3)
      { model: 'Sport_Soft',  label: 'Sport Tendre',  category: 'Sport',  gripBonus:  0.12, wearRateMultiplier: 1.0,  upgradeLevel: 1, buyCost: Math.round(7000 * m), changeCost: Math.round(1500 * m), description: "Semi-slick tendre. Tres bon grip, duree correcte." },
      { model: 'Sport',       label: 'Sport Normal',  category: 'Sport',  gripBonus:  0.08, wearRateMultiplier: 0.85, upgradeLevel: 1, buyCost: Math.round(6000 * m), changeCost: Math.round(1200 * m), description: "Semi-slick. Bon grip, durabilite correcte." },
      { model: 'Sport_Hard',  label: 'Sport Dur',     category: 'Sport',  gripBonus:  0.04, wearRateMultiplier: 0.7,  upgradeLevel: 1, buyCost: Math.round(5000 * m), changeCost: Math.round(900 * m),  description: "Semi-slick dur. Durabilite superieure, grip modere." },
      // Racing (5)
      { model: 'Racing_SuperSoft', label: 'Racing SSoft',  category: 'Racing', gripBonus: 0.28, wearRateMultiplier: 3.5,  upgradeLevel: 2, buyCost: Math.round(30000 * m), changeCost: Math.round(8000 * m), description: "Tres tendre. Grip maximum, s use a chaque virage." },
      { model: 'Racing_Soft',      label: 'Racing Soft',   category: 'Racing', gripBonus: 0.22, wearRateMultiplier: 2.5,  upgradeLevel: 2, buyCost: Math.round(25000 * m), changeCost: Math.round(6500 * m), description: "Tendre. Excellent grip, duree de vie courte." },
      { model: 'Racing_Medium',    label: 'Racing Medium', category: 'Racing', gripBonus: 0.16, wearRateMultiplier: 1.6,  upgradeLevel: 2, buyCost: Math.round(20000 * m), changeCost: Math.round(5000 * m), description: "Normal. Bon equilibre performance/duree de vie." },
      { model: 'Racing_Hard',      label: 'Racing Hard',   category: 'Racing', gripBonus: 0.10, wearRateMultiplier: 1.0,  upgradeLevel: 2, buyCost: Math.round(22000 * m), changeCost: Math.round(5500 * m), description: "Dur. Durabilite elevee, grip legerement inferieur." },
      { model: 'Racing_SuperHard', label: 'Racing SHard',  category: 'Racing', gripBonus: 0.04, wearRateMultiplier: 0.7,  upgradeLevel: 2, buyCost: Math.round(18000 * m), changeCost: Math.round(4500 * m), description: "Tres dur. Longevite max, grip reduit." },
    ];
  }

  // ── Achat upgrade standard ────────────────────────────
  buy(type: UpgradeCategory) {
    if (!this.user || !this.selectedCar || this.buying) return;
    this.buying = type;
    this.lastResult = null;
    this.upgradeService.buyUpgrade(this.user.id, this.selectedCar.id, type).subscribe({
      next: result => {
        this.buying = null;
        this.lastResult = result;
        this.syncState(result);
        this.scheduleResultDismiss();
      },
      error: err => { this.buying = null; alert('❌ ' + (err.error || 'Erreur')); }
    });
  }

  // ── Achat pneus ───────────────────────────────────────
  buyTires(tireModel: string) {
    if (!this.user || !this.selectedCar || this.buying) return;
    this.buying = 'TIRES_' + tireModel;
    this.lastResult = null;
    this.upgradeService.buyTires(this.user.id, this.selectedCar.id, tireModel).subscribe({
      next: result => {
        this.buying = null;
        this.lastResult = result;
        this.syncState(result);
        // Met à jour le tireModel local
        if (this.selectedCar) this.selectedCar.tireModel = tireModel;
        this.loadTiresCatalog();
        this.scheduleResultDismiss();
      },
      error: err => { this.buying = null; alert('❌ ' + (err.error || 'Erreur')); }
    });
  }

  private syncState(result: UpgradeResult) {
    if (this.upgrades) {
      this.upgrades.engineLevel       = result.engineLevel;
      this.upgrades.transmissionLevel = result.transmissionLevel;
      this.upgrades.suspensionLevel   = result.suspensionLevel;
      this.upgrades.brakesLevel       = result.brakesLevel;
      this.upgrades.weightLevel       = result.weightLevel;
      this.upgrades.tiresLevel        = result.tiresLevel;
    }
    if (this.selectedCar) {
      this.selectedCar.power        = result.powerAfter;
      this.selectedCar.weight       = result.weightAfter;
      this.selectedCar.gripModifier = result.gripAfter;
      this.selectedCar.tireType     = result.tireAfter;
    }
    if (this.user) {
      this.user.credits = result.newBalance;
      localStorage.setItem('currentUser', JSON.stringify(this.user));
    }
  }

  private scheduleResultDismiss() {
    clearTimeout(this.resultTimeout);
    this.resultTimeout = setTimeout(() => this.lastResult = null, 4000);
  }

  // ── Helpers ────────────────────────────────────────────
  getLevel(def: UpgradeDef): number {
    if (!this.upgrades) return 0;
    const v = this.upgrades[def.levelKey];
    return typeof v === 'number' ? v : 0;
  }

  levelLabel(level: number): string  { return ['Stock', 'Sport', 'Racing'][level] ?? 'Racing'; }
  levelClass(level: number): string  { return ['level-stock', 'level-sport', 'level-racing'][level] ?? 'level-racing'; }
  isMaxed(def: UpgradeDef): boolean  { return this.getLevel(def) >= 2; }

  estimatedCost(def: UpgradeDef): number {
    if (!this.selectedCar) return 0;
    const carPrice  = Math.max(this.selectedCar.price, 5000);
    const level     = this.getLevel(def) + 1;
    let tierMult: number;
    if      (carPrice < 15000)  tierMult = 1.2;
    else if (carPrice < 50000)  tierMult = 1.8;
    else if (carPrice < 120000) tierMult = 2.4;
    else                        tierMult = 3.5;
    const basePrice = Math.round(carPrice * tierMult);
    const baseRatios: Record<UpgradeCategory, number> = {
      ENGINE: 0.16, TRANSMISSION: 0.12, SUSPENSION: 0.11,
      BRAKES: 0.10, WEIGHT: 0.14, TIRES: 0.09
    };
    const baseRatio = baseRatios[def.type] ?? 0.12;
    const levelMult = level === 1 ? 1.0 : 2.8;
    return Math.max(1000, Math.round(basePrice * baseRatio * levelMult));
  }

  canAfford(def: UpgradeDef): boolean {
    return (this.user?.credits ?? 0) >= this.estimatedCost(def);
  }

  canAffordTire(entry: TireCatalogEntry): boolean {
    return (this.user?.credits ?? 0) >= entry.buyCost;
  }

  get tiresStreet():  TireCatalogEntry[] { return this.tiresCatalog.filter(t => t.category === 'Street'); }
  get tiresSport():   TireCatalogEntry[] { return this.tiresCatalog.filter(t => t.category === 'Sport'); }
  get tiresRacing():  TireCatalogEntry[] { return this.tiresCatalog.filter(t => t.category === 'Racing'); }

  get currentTireModel(): string { return this.selectedCar?.tireModel ?? 'Street'; }

  isCurrentTire(model: string): boolean { return this.selectedCar?.tireModel === model; }

  wearColor(rate: number): string {
    if (rate <= 1.0) return 'cyan';
    if (rate <= 2.0) return 'gold';
    return 'mag';
  }

  wearLabel(rate: number): string {
    if (rate <= 0.8) return 'Très longue durée';
    if (rate <= 1.0) return 'Longue durée';
    if (rate <= 1.6) return 'Durée normale';
    if (rate <= 2.5) return 'Courte durée';
    return 'Très courte durée';
  }

  // Stat display helpers
  powerPercent(p: number): number  { return Math.min((p / 600) * 100, 100); }
  weightPercent(w: number): number { return Math.max(0, 100 - ((w - 500) / 1500) * 100); }
  gripPercent(g: number): number   { return Math.min(((g - 1.0) / 0.6) * 100, 100); }
  tierLabel(power: number): string {
    if (power >= 400) return 'S'; if (power >= 300) return 'A';
    if (power >= 200) return 'B'; if (power >= 150) return 'C'; return 'D';
  }

  goBack()        { this.router.navigate(['/dashboard']); }
  goToDealership(){ this.router.navigate(['/dealership']); }
}
