import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/class/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent {

  user: User = new User();

  constructor(
    private router: Router,
    private userService: UserService
  ) {}

  onSubmit() {
    this.userService.save(this.user).subscribe({
      next: () => this.router.navigate(['/']),
      error: err => alert('Erreur : ' + (err.error || 'Nom déjà pris ?'))
    });
  }
}