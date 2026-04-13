import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Car } from 'src/app/class/car';
import { Dealership } from 'src/app/class/dealership';
import { User } from 'src/app/class/user';
import { DealershipService } from 'src/app/services/dealership.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-dealership-detail',
  templateUrl: './dealership-detail.component.html',
  styleUrls: ['./dealership-detail.component.css']
})
export class DealershipDetailComponent implements OnInit {

  dealership: Dealership | undefined;
  sessionUser: User | null = null;
  buyingCarId: number | null = null;
  buyFeedback = '';

  constructor(
    private activatedRoute: ActivatedRoute,
    private dealershipService: DealershipService,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit() {
    const id = Number(this.activatedRoute.snapshot.paramMap.get('id'));
    if (id) {
      this.dealershipService.findById(id).subscribe(data => {
        this.dealership = data;
      });
    }
    // Charge TOUJOURS les données fraîches depuis le serveur
    const s = this.userService.getSession();
    if (s) {
      this.userService.findById(s.id).subscribe(u => {
        this.sessionUser = u;
        // Met aussi à jour la session locale
        localStorage.setItem('currentUser', JSON.stringify(u));
      });
    }
  }

  get playerCredits(): number { return this.sessionUser?.credits ?? 0; }
  canAfford(car: Car): boolean { return this.playerCredits >= car.price; }

  buyCar(car: Car) {
    if (!this.sessionUser) { this.router.navigate(['/']); return; }
    if (!this.canAfford(car)) return;
    this.buyingCarId = car.id;

    this.userService.buyCar(this.sessionUser, car).subscribe({
      next: () => {
        this.buyingCarId = null;
        // Recharge les données fraîches depuis le serveur pour avoir le bon solde
        this.userService.findById(this.sessionUser!.id).subscribe(u => {
          this.sessionUser = u;
          localStorage.setItem('currentUser', JSON.stringify(u));
          sessionStorage.setItem('userId', String(u.id));
        });
        if (this.dealership?.cars) {
          this.dealership.cars = this.dealership.cars.filter(c => c.id !== car.id);
        }
        this.buyFeedback = car.name + ' ajoutée au garage !';
        setTimeout(() => this.buyFeedback = '', 3000);
      },
      error: err => {
        this.buyingCarId = null;
        alert('Erreur : ' + (err.error || 'Achat impossible'));
      }
    });
  }

  tierLabel(power: number): string {
    if (power >= 400) return 'S';
    if (power >= 300) return 'A';
    if (power >= 200) return 'B';
    if (power >= 150) return 'C';
    return 'D';
  }

  goToDealership() { this.router.navigate(['/dealership']); }
}