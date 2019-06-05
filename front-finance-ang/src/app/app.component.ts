import {Component} from '@angular/core';
import {ApiService} from './api.service';
import {MatSnackBar} from '@angular/material';
import {showSnackbar} from '../utils/frontComponents';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(public apiService: ApiService,
              private snackbar: MatSnackBar){ }

  logout() {
    this.apiService.logout();
    showSnackbar(this.snackbar, 'Logout successful!');
  }

  getAccountsNumbers(): String[] {
    return this.apiService.getAccounts().map(account => account.number);
  }
}
