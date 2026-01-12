import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Dealership } from '../class/dealership';

@Injectable({
  providedIn: 'root'
})
export class DealershipService {

  private dealershipUrl = 'http://localhost:8080/api/dealership';

  constructor(private http: HttpClient) { }

  public findAll() : Observable<Dealership[]> {
    return this.http.get<Dealership[]>(this.dealershipUrl);
  }

  public findById(id: number) : Observable<Dealership>{
    return this.http.get<Dealership>(`${this.dealershipUrl}/${id}`)
  }
}
