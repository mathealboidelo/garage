import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { User } from '../class/user';
import { Car } from '../class/car';

@Injectable({ providedIn: 'root' })
export class UserService {

  private usersUrl   = 'http://localhost:8080/api/users';
  private deleteUrl  = 'http://localhost:8080/api/deleteuser';
  private buyCarApi  = 'http://localhost:8080/api/buy';

  constructor(private http: HttpClient) {}

  public save(user: User): Observable<User> {
    return this.http.post<User>(this.usersUrl, user);
  }

  public deleteUser(id: number): Observable<any> {
    return this.http.delete<any>(`${this.deleteUrl}/${id}`);
  }

  public findAll(): Observable<User[]> {
    return this.http.get<User[]>(this.usersUrl);
  }

  public findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.usersUrl}/${id}`);
  }

  /** Alias utilisé dans ParkingDetailComponent */
  public getUserById(id: number): Observable<User> {
    return this.findById(id);
  }

  // ── Session (localStorage) ─────────────────────────────

  public setSession(user: User): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    // Stocke aussi l'id seul pour un accès rapide
    sessionStorage.setItem('userId', String(user.id));
  }

  public getSession(): User | null {
    const raw = localStorage.getItem('currentUser');
    return raw ? JSON.parse(raw) : null;
  }

  public logout(): void {
    localStorage.removeItem('currentUser');
    sessionStorage.removeItem('userId');
  }

  // ── Actions ───────────────────────────────────────────

  public buyCar(user: User | null, car: Car): Observable<string> {
    return this.http.post(
      this.buyCarApi,
      { userId: user?.id, carId: car.id },
      { responseType: 'text' }
    );
  }

  public cheatRep(): Observable<string> {
    const sessionUser = this.getSession();
    return this.http.post(
      `http://localhost:8080/api/cheatrep/${sessionUser?.id}`,
      {},
      { responseType: 'text' }
    );
  }

  public cheat(): Observable<string> {
    const sessionUser = this.getSession();
    return this.http.post(
      `http://localhost:8080/api/cheatmoney/${sessionUser?.id}`,
      {},
      { responseType: 'text' }
    );
  }

  /** Recharge les données fraîches et met à jour la session */
  public refreshSession(id: number): Observable<User> {
    return this.findById(id).pipe(
      tap(u => localStorage.setItem('currentUser', JSON.stringify(u)))
    );
  }
}
