import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Parking } from '../class/parking';

@Injectable({
  providedIn: 'root'
})
export class ParkingService {

  private parkingUrl = 'http://localhost:8080/api/dealership';

  constructor(private http: HttpClient) { }

  public findAll() : Observable<Parking[]>{
    return this.http.get<Parking[]>(this.parkingUrl)
  }

  public findById(id: number) : Observable<Parking>{
      return this.http.get<Parking>(`${this.parkingUrl}/${id}`)
    }
}
