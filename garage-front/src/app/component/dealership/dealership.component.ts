import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Dealership } from 'src/app/class/dealership';
import { DealershipService } from 'src/app/services/dealership.service';

@Component({
  selector: 'app-dealership',
  templateUrl: './dealership.component.html',
  styleUrls: ['./dealership.component.css']
})
export class DealershipComponent implements OnInit {

  dealerships: Dealership[] = [];

  constructor(
    private dealershipService: DealershipService,
    private router: Router
  ) {}

  ngOnInit() {
    this.dealershipService.findAll().subscribe(data => {
      this.dealerships = data;
    });
  }

  visitDealership(id: number) {
    this.router.navigate(['/dealership', id]);
  }

  goToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
