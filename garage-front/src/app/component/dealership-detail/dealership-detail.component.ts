import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Dealership } from 'src/app/class/dealership';
import { DealershipService } from 'src/app/services/dealership.service';

@Component({
  selector: 'app-dealership-detail',
  templateUrl: './dealership-detail.component.html',
  styleUrls: ['./dealership-detail.component.css']
})
export class DealershipDetailComponent implements OnInit{

  dealership: Dealership | undefined;

  constructor(private activatedRoute : ActivatedRoute, private dealershipService: DealershipService, private router: Router) {}

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

  buyCar(){

  }

  goToDealership(){
    this.router.navigate(['/dealership']);
  }
}
