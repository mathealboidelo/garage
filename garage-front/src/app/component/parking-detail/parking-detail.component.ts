import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Parking } from 'src/app/class/parking';
import { ParkingService } from 'src/app/services/parking.service';

@Component({
  selector: 'app-parking-detail',
  templateUrl: './parking-detail.component.html',
  styleUrls: ['./parking-detail.component.css']
})
export class ParkingDetailComponent implements OnInit{

  parking : Parking | undefined;

  constructor(private activatedRoute : ActivatedRoute, private parkingsService : ParkingService) {}

  ngOnInit() {
    const idString = this.activatedRoute.snapshot.paramMap.get('id');

    const id = Number(idString);

    if(id){
      this.parkingsService.findById(id).subscribe( u => {
        this.parking = u;
      })
    }
  }

}
