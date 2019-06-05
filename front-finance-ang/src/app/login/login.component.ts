import {Component, OnInit} from '@angular/core';
import {ApiService} from '../api.service';
import {showSnackbar} from '../../utils/frontComponents';
import {MatSnackBar} from "@angular/material";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(private apiService: ApiService,
              private snackbar: MatSnackBar,
              private router: Router) {

    if (apiService.isLoggedIn()) {
      router.navigate(['/dashboard']);
    }
  }

  ngOnInit() {
  }

  tryToLogin(login: string, password: string){
    this.apiService.login(login, password)
      .then(response => {
        this.apiService.saveLoginData(response.access_token, login)
          .then(() => {
            showSnackbar(this.snackbar, 'Login successful!');
            // console.log(this.apiService.getCurrentAccount());
            this.router.navigate(['/dashboard']);
          });
      })
      .catch(error => showSnackbar(this.snackbar, 'Error during login (error ' + error.status + ')'));
  }

  tryToRegister(login: string, password: string){
    if (login.trim() === '' || password.trim() === '') {
      showSnackbar(this.snackbar, 'Błąd: Pola "Login" oraz "Hasło" są obowiązkowe do rejestracji użytkownika');
    } else {
      this.apiService.register(login, password)
        .then(() => {
          showSnackbar(this.snackbar, 'Zarejetrowano poprawnie, trwa logowanie...');
          this.tryToLogin(login, password);
        })
        .catch(error => {
          if (error.status === 400){
            showSnackbar(this.snackbar, 'Błąd: Użytkownik o podanej nazwie już istnieje');
          }
          else {
            showSnackbar(this.snackbar, 'Problem z rejestracją (error ' + error.status + ')');
          }
        });
    }
  }

}
