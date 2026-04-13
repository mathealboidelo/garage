import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Parking } from 'src/app/class/parking';
import { ParkingService } from 'src/app/services/parking.service';

@Component({
  selector: 'app-parking',
  templateUrl: './parking.component.html',
  styleUrls: ['./parking.component.css']
})
export class ParkingComponent implements OnInit {

  parkings: Parking[] = [];
  loading = true;

  constructor(
    private parkingService: ParkingService,
    private router: Router
  ) {}

  ngOnInit() {
    this.parkingService.findAll().subscribe({
      next: p => { this.parkings = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  visitParking(id: number) {
    this.router.navigate(['/parkings', id]);
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
