import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Dealership } from 'src/app/class/dealership';
import { User } from 'src/app/class/user';
import { DealershipService } from 'src/app/services/dealership.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  user: User | null = null;
  dealerships: Dealership[] = [];

  constructor(private userService: UserService, private dealershipService: DealershipService,private router: Router) {}

  ngOnInit() {
    // 1. On regarde s'il y a quelqu'un en mémoire
    const sessionUser = this.userService.getSession();

    if (!sessionUser) {
      // Si personne n'est connecté, retour à l'accueil !
      this.router.navigate(['/']);
    } else {
      // 2. Optionnel : On recharge les données fraîches depuis le serveur
      // (Utile si les crédits ont changé entre temps)
      this.userService.findById(sessionUser.id).subscribe(u => {
          this.user = u;
      });

      this.dealershipService.findAll().subscribe(data => {
        this.dealerships = data; // On remplit la liste avec la réponse de Java
        console.log("Concessionnaires chargés :", this.dealerships);
      });
    }
  }

  logout() {
    this.userService.logout();
    this.router.navigate(['/']);
  }
}
