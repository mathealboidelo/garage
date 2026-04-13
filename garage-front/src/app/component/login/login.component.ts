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

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.userService.findAll().subscribe(data => {
      this.users = data;
    });
  }

  selectProfile(user: User) {
    this.userService.setSession(user); // sauvegarde localStorage + sessionStorage userId
    this.router.navigate(['/dashboard']);
  }

  deleteProfile(id: number, event: MouseEvent) {
    event.stopPropagation();
    if (confirm('Supprimer ce pilote définitivement ?')) {
      this.userService.deleteUser(id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== id);
        },
        error: err => console.error('Erreur suppression', err)
      });
    }
  }
}
