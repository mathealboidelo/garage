import { Component, OnInit,  inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from './services/user.service'; // On importe ton service

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  user: any; 

  // 2. On injecte le service (approche moderne avec inject)
  private userService = inject(UserService);

  // 3. Cette méthode s'exécute automatiquement au démarrage du composant
  ngOnInit(): void {

  }


}
