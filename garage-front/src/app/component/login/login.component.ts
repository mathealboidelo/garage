import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/class/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  users: User[] = [];

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit() {
    // On charge la liste des profils dispos
    this.userService.findAll().subscribe(data => {
      this.users = data;
    });
  }

  deleteProfile(id: number, event: MouseEvent) {
    // 1. Empêche le clic de se propager à la carte (évite de charger le profil)
    event.stopPropagation();

    // Confirmation avant suppression (optionnel mais conseillé)
    if (confirm("Es-tu sûr de vouloir supprimer ce pilote ?")) {
      
      // 2. On appelle le service et on S'ABONNE (très important, sinon rien ne se passe)
      this.userService.deleteUser(id).subscribe({
        next: () => {
          // 3. On filtre la liste locale pour faire disparaître l'utilisateur
          this.users = this.users.filter(u => u.id !== id);
          console.log("Utilisateur supprimé avec succès");
        },
        error: (err) => {
          console.error("Erreur lors de la suppression", err);
        }
      });
    }
  }

  // Quand on clique sur une carte
  selectProfile(user: User) {
    console.log("Objet utilisateur complet :", user);
    console.log("Profil choisi :", user.username);
    console.log("id du profil :", user.id);
    this.userService.setSession(user); // On sauvegarde dans le localStorage
    this.router.navigate(['/dashboard']); // On part vers le tableau de bord
  }
}
