import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Parking } from 'src/app/class/parking';
import { ParkingService } from 'src/app/services/parking.service';

@Component({
  selector: 'app-parking',
  templateUrl: './parking.component.html',
  styleUrls: ['./parking.component.css']
})
export class ParkingComponent {

  parkings : Parking[] = [];

  constructor(private parkingService: ParkingService, private router: Router) {}
  
  ngOnInit(){
    this.parkingService.findAll().subscribe(u => {
      this.parkings = u;
    });
  }

  visitParking(id: Number){
    this.router.navigate(['/parkings', id])
  }

  stayGarage(){
    this.router.navigate(['/dashboard']);
  }
}
