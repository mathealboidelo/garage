import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/class/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  user: User | null = null;
  loading = true;
  cheatFeedback = '';

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    const s = this.userService.getSession();
    if (!s) { this.router.navigate(['/']); return; }
    this.userService.findById(s.id).subscribe({
      next: u => {
        this.user = u; this.loading = false;
        localStorage.setItem('currentUser', JSON.stringify(u));
        sessionStorage.setItem('userId', String(u.id));
      },
      error: () => { this.user = s; this.loading = false; }
    });
  }

  cheat() {
    this.userService.cheat().subscribe(() => {
      if (this.user) { this.user.credits += 10000; this.cheatFeedback = '+10 000 CR'; setTimeout(() => this.cheatFeedback = '', 2000); }
    });
  }

  logout()          { this.userService.logout(); this.router.navigate(['/']); }
  goToDealership()  { this.router.navigate(['/dealership']); }
  goToParkings()    { this.router.navigate(['/parkings']); }
  goToUpgrade()     { this.router.navigate(['/upgrade']); }
  goToAutoShop()    { this.router.navigate(['/autoshop']); }

  get firstCar()  { return this.user?.garage?.cars?.[0] ?? null; }
  get hasGarage() { return (this.user?.garage?.cars?.length ?? 0) > 0; }

  tierLabel(p: number) {
    if (p >= 400) return 'S'; if (p >= 300) return 'A';
    if (p >= 200) return 'B'; if (p >= 150) return 'C'; return 'D';
  }
  powerPercent(p: number)  { return Math.min((p / 500) * 100, 100); }
  weightPercent(w: number) { return Math.max(0, 100 - ((w - 500) / 1500) * 100); }
  tireColor(v: number)     { return v > 60 ? 'cyan' : v > 30 ? 'gold' : 'mag'; }
  oilColor(v: number)      { return v > 50 ? 'cyan' : v > 25 ? 'gold' : 'mag'; }
  repPercent(rep: number)  { return Math.min((rep % 500) / 5, 100); }  // % vers prochain niveau
}
