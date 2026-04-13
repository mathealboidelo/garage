import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Car } from 'src/app/class/car';
import { CarUpgrade, UpgradeCategory, UpgradeResult } from 'src/app/class/car-upgrade';
import { User } from 'src/app/class/user';
import { UpgradeService } from 'src/app/services/upgrade.service';
import { UserService } from 'src/app/services/user.service';

export interface UpgradeDef {
  type: UpgradeCategory;
  label: string;
  icon: string;
  description: string;
  statAffected: string;
  levelKey: keyof CarUpgrade;
  costs: [number, number]; // [sport cost, racing cost] — overridden by API
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
  buying: UpgradeCategory | null = null;     // spinner state
  lastResult: UpgradeResult | null = null;
  resultTimeout: any;

  // ── Catalogue des upgrades ──────────────────────────────
  readonly catalog: UpgradeDef[] = [
    {
      type: 'ENGINE',
      label: 'Moteur',
      icon: '⚙',
      description: 'Augmente significativement la puissance. Idéal pour les circuits en ligne droite.',
      statAffected: 'power',
      levelKey: 'engineLevel',
      costs: [0, 0]
    },
    {
      type: 'TRANSMISSION',
      label: 'Transmission',
      icon: '⚡',
      description: 'Améliore la transmission du couple aux roues. Gain de puissance + grip léger.',
      statAffected: 'power',
      levelKey: 'transmissionLevel',
      costs: [0, 0]
    },
    {
      type: 'SUSPENSION',
      label: 'Suspension',
      icon: '🔧',
      description: 'Kit sport ou racing pour une meilleure tenue en virage. Essentiel pour le touge.',
      statAffected: 'grip',
      levelKey: 'suspensionLevel',
      costs: [0, 0]
    },
    {
      type: 'BRAKES',
      label: 'Freins',
      icon: '🛑',
      description: 'Disques et étriers renforcés. Meilleure décélération = meilleure sortie de virage.',
      statAffected: 'grip',
      levelKey: 'brakesLevel',
      costs: [0, 0]
    },
    {
      type: 'WEIGHT',
      label: 'Allègement',
      icon: '⬇',
      description: 'Pièces en fibre de carbone et titane. Réduction de poids pour accélérer plus vite.',
      statAffected: 'weight',
      levelKey: 'weightLevel',
      costs: [0, 0]
    },
    {
      type: 'TIRES',
      label: 'Pneus',
      icon: '○',
      description: 'Semi-Slick (Sport) ou Slick Racing. Amélioration massive du grip global.',
      statAffected: 'grip',
      levelKey: 'tiresLevel',
      costs: [0, 0]
    }
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
        if (u.garage?.cars?.length > 0) {
          this.selectCar(u.garage.cars[0]);
        } else {
          this.loading = false;
        }
      },
      error: () => { this.loading = false; }
    });
  }

  selectCar(car: Car) {
    this.selectedCar = car;
    this.upgrades = null;
    this.loading = true;
    this.upgradeService.getUpgrades(car.id).subscribe({
      next: u => { this.upgrades = u; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  // ── Achat d'un upgrade ─────────────────────────────────
  buy(type: UpgradeCategory) {
    if (!this.user || !this.selectedCar || this.buying) return;
    this.buying = type;
    this.lastResult = null;

    this.upgradeService.buyUpgrade(this.user.id, this.selectedCar.id, type).subscribe({
      next: result => {
        this.buying = null;
        this.lastResult = result;

        // Sync local state
        if (this.upgrades) {
          this.upgrades.engineLevel       = result.engineLevel;
          this.upgrades.transmissionLevel = result.transmissionLevel;
          this.upgrades.suspensionLevel   = result.suspensionLevel;
          this.upgrades.brakesLevel       = result.brakesLevel;
          this.upgrades.weightLevel       = result.weightLevelVal;
          this.upgrades.tiresLevel        = result.tiresLevel;
        }
        if (this.selectedCar) {
          this.selectedCar.power       = result.powerAfter;
          this.selectedCar.weight      = result.weightAfter;
          this.selectedCar.gripModifier = result.gripAfter;
          this.selectedCar.tireType    = result.tireAfter;
        }
        if (this.user) {
          this.user.credits = result.newBalance;
          localStorage.setItem('currentUser', JSON.stringify(this.user));
        }
        // Auto-dismiss après 4s
        clearTimeout(this.resultTimeout);
        this.resultTimeout = setTimeout(() => this.lastResult = null, 4000);
      },
      error: err => {
        this.buying = null;
        alert('❌ ' + (err.error || 'Erreur lors de l\'upgrade'));
      }
    });
  }

  // ── Helpers ────────────────────────────────────────────
  getLevel(def: UpgradeDef): number {
    if (!this.upgrades) return 0;
    const v = this.upgrades[def.levelKey];
    return typeof v === 'number' ? v : 0;
  }

  levelLabel(level: number): string {
    return ['Stock', 'Sport', 'Racing'][level] ?? 'Racing';
  }

  levelClass(level: number): string {
    return ['level-stock', 'level-sport', 'level-racing'][level] ?? 'level-racing';
  }

  isMaxed(def: UpgradeDef): boolean {
    return this.getLevel(def) >= 2;
  }

  // Coût estimatif basé sur le prix voiture (avant réponse API)
  estimatedCost(def: UpgradeDef): number {
    if (!this.selectedCar) return 0;
    const base = Math.max(this.selectedCar.price, 5000);
    const level = this.getLevel(def) + 1;
    const ratios: Record<UpgradeCategory, [number, number]> = {
      ENGINE:       [0.12, 0.22],
      TRANSMISSION: [0.08, 0.15],
      SUSPENSION:   [0.07, 0.13],
      BRAKES:       [0.06, 0.11],
      WEIGHT:       [0.10, 0.20],
      TIRES:        [0.05, 0.10]
    };
    const ratio = ratios[def.type][level - 1] ?? 0.1;
    return Math.max(500, Math.round(base * ratio));
  }

  canAfford(def: UpgradeDef): boolean {
    return (this.user?.credits ?? 0) >= this.estimatedCost(def);
  }

  // ── Stat display helpers ───────────────────────────────
  powerPercent(power: number): number {
    return Math.min((power / 600) * 100, 100);
  }
  weightPercent(weight: number): number {
    return Math.max(0, 100 - ((weight - 500) / 1500) * 100);
  }
  gripPercent(grip: number): number {
    return Math.min(((grip - 1.0) / 0.6) * 100, 100);
  }

  tierLabel(power: number): string {
    if (power >= 400) return 'S';
    if (power >= 300) return 'A';
    if (power >= 200) return 'B';
    if (power >= 150) return 'C';
    return 'D';
  }

  goBack() { this.router.navigate(['/dashboard']); }
  goToDealership() { this.router.navigate(['/dealership']); }
}
