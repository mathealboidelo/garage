import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../class/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  // URL pour le dashboard (1 seul joueur)
  private playerUrl = 'http://localhost:8080/api/player';
  // URL pour la gestion (liste et ajout)
  private usersUrl = 'http://localhost:8080/api/users';

  private deleteUrl = 'http://localhost:8080/api/deleteuser';

  constructor(private http: HttpClient) { }

  // Appelé par UserFormComponent (Ajout)
  public save(user: User) {
    return this.http.post<User>(this.usersUrl, user);
  }

  public deleteUser(id: number): Observable<any>{
    return this.http.delete<User>(`${this.deleteUrl}/${id}`);
  }

  // Appelé par UserListComponent (Liste)
  public findAll(): Observable<User[]> {
    return this.http.get<User[]>(this.usersUrl);
  }

  public findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.usersUrl}/${id}`);
  }

  public setSession(user: User): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
  }

  public getSession(): User | null {
    const userJson = localStorage.getItem('currentUser');
    return userJson ? JSON.parse(userJson) : null;
  }

  // 4. Déconnexion
  public logout(): void {
    localStorage.removeItem('currentUser');
  }
}
