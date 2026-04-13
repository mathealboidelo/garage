import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Car } from 'src/app/class/car';
import { User } from 'src/app/class/user';
import { UserService } from 'src/app/services/user.service';
import { AutoShopService, CostInfo } from 'src/app/services/auto-shop.service';

@Component({
  selector: 'app-auto-shop',
  templateUrl: './auto-shop.component.html',
  styleUrls: ['./auto-shop.component.css']
})
export class AutoShopComponent implements OnInit {

  user: User | null = null;
  selectedCar: Car | null = null;
  costs: CostInfo | null = null;
  loading = true;
  working: 'tires' | 'oil' | 'sell' | null = null;
  feedback = '';
  feedbackType: 'ok' | 'err' = 'ok';

  constructor(
    private userService: UserService,
    private autoShopService: AutoShopService,
    private router: Router
  ) {}

  ngOnInit() {
    const s = this.userService.getSession();
    if (!s) { this.router.navigate(['/']); return; }
    this.userService.findById(s.id).subscribe(u => {
      this.user = u;
      if (u.garage?.cars?.length > 0) this.selectCar(u.garage.cars[0]);
      this.loading = false;
    });
  }

  selectCar(car: Car) {
    this.selectedCar = car;
    this.costs = null;
    this.autoShopService.getCosts(car.id).subscribe(c => this.costs = c);
  }

  changeTires() {
    if (!this.user || !this.selectedCar) return;
    this.working = 'tires';
    this.autoShopService.changeTires(this.selectedCar.id, this.user.id).subscribe({
      next: r => {
        this.working = null;
        if (this.selectedCar) {
          this.selectedCar.tireWear = r.newTireWear;
        }
        if (this.user) this.user.credits = r.newBalance;
        this.showFeedback(r.message, 'ok');
        this.updateSession();
      },
      error: e => { this.working = null; this.showFeedback(e.error, 'err'); }
    });
  }

  changeOil() {
    if (!this.user || !this.selectedCar) return;
    this.working = 'oil';
    this.autoShopService.changeOil(this.selectedCar.id, this.user.id).subscribe({
      next: r => {
        this.working = null;
        if (this.selectedCar) this.selectedCar.oilQuality = r.newOilQuality;
        if (this.user) this.user.credits = r.newBalance;
        this.showFeedback(r.message, 'ok');
        this.updateSession();
      },
      error: e => { this.working = null; this.showFeedback(e.error, 'err'); }
    });
  }

  sellCar() {
    if (!this.user || !this.selectedCar) return;
    if (!confirm(`Vendre ${this.selectedCar.name} pour ${this.costs?.saleValue?.toLocaleString()} CR ?`)) return;
    this.working = 'sell';
    this.autoShopService.sellCar(this.selectedCar.id, this.user.id).subscribe({
      next: r => {
        this.working = null;
        if (this.user) {
          this.user.credits = r.newBalance;
          this.user.garage.cars = this.user.garage.cars.filter(c => c.id !== this.selectedCar!.id);
          this.selectedCar = this.user.garage.cars[0] ?? null;
          if (this.selectedCar) this.autoShopService.getCosts(this.selectedCar.id).subscribe(c => this.costs = c);
          else this.costs = null;
        }
        this.showFeedback(r.message, 'ok');
        this.updateSession();
      },
      error: e => { this.working = null; this.showFeedback(e.error, 'err'); }
    });
  }

  private updateSession() {
    if (this.user) localStorage.setItem('currentUser', JSON.stringify(this.user));
  }

  private showFeedback(msg: string, type: 'ok' | 'err') {
    this.feedback = msg; this.feedbackType = type;
    setTimeout(() => this.feedback = '', 4000);
  }

  tireColor(v: number) { return v > 60 ? 'cyan' : v > 30 ? 'gold' : 'mag'; }
  oilColor(v: number)  { return v > 50 ? 'cyan' : v > 25 ? 'gold' : 'mag'; }
  wearLabel(v: number) { return v > 60 ? 'Bon état' : v > 30 ? 'Usé' : '⚠ Critique'; }
  oilLabel(v: number)  { return v > 50 ? 'Propre' : v > 25 ? 'À surveiller' : '⚠ Vidange urgente'; }
  powerPercent(p: number) { return Math.min((p / 600) * 100, 100); }
  weightPercent(w: number){ return Math.max(0, 100 - ((w - 500) / 1500) * 100); }
  gripPercent(g: number)  { return Math.min(((g - 1.0) / 0.6) * 100, 100); }
  tierLabel(p: number) {
    if (p >= 400) return 'S'; if (p >= 300) return 'A';
    if (p >= 200) return 'B'; if (p >= 150) return 'C'; return 'D';
  }
  canAffordTires() { return (this.user?.credits ?? 0) >= (this.costs?.tireCost ?? 0); }
  canAffordOil()   { return (this.user?.credits ?? 0) >= (this.costs?.oilCost  ?? 0); }
  canSell()        { return (this.user?.garage?.cars?.length ?? 0) > 1; }
  goBack() { this.router.navigate(['/dashboard']); }
}
