import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Car } from 'src/app/class/car';
import { Dealership } from 'src/app/class/dealership';
import { DealershipService } from 'src/app/services/dealership.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-dealership-detail',
  templateUrl: './dealership-detail.component.html',
  styleUrls: ['./dealership-detail.component.css']
})
export class DealershipDetailComponent implements OnInit{

  dealership: Dealership | undefined;

  

  constructor(private activatedRoute : ActivatedRoute, private dealershipService: DealershipService, private router: Router,private userService: UserService) {}

  ngOnInit() {
    const idString = this.activatedRoute.snapshot.paramMap.get('id');

    const id = Number(idString);

    console.log("ID récupéré de l'URL :", id);

    if(id){
      this.dealershipService.findById(id).subscribe(data => {
        this.dealership = data;
        console.log("Concessionnaire chargé :", this.dealership);
      })

    }
  }

  buyCar(car: Car) {
  const sessionUser = this.userService.getSession();
  
  if (sessionUser) {
    this.userService.buyCar(sessionUser, car).subscribe({
      next: (response) => {
        alert("Félicitations ! Vous avez acheté la " + car.name);
        // Ici, tu devrais retirer la voiture de la liste affichée pour faire "propre"
      },
      error: (err) => {
        alert("Erreur : " + err.error); // Affiche "Pas assez d'argent !"
      }
    });
  }
}

  goToDealership(){
    this.router.navigate(['/dealership']);
  }
}
