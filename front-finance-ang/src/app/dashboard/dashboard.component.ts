import { Component, OnInit } from '@angular/core';
import {Transaction} from '../../dataModel/transaction';
import {ApiService} from '../api.service';
import {Router} from '@angular/router';
import {formatDate} from '@angular/common';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  transactions: Transaction[] = [];
  accountBalance: number;
  pageNum: number = 1;

  constructor(private apiService: ApiService, private router: Router) {
    if (!apiService.isLoggedIn()) {
      router.navigate(['/login']);
    }
  }

  ngOnInit() {
    let accountNumber: string = this.apiService.getCurrentAccountNumber();
    this.apiService.getTransactions(accountNumber)
      .subscribe(response => {
        response.forEach(element => this.transactions.push(element));
        console.log(this.transactions);
    });
    this.apiService.getAccountBalance(accountNumber)
      .subscribe(response => {
        this.accountBalance = response.totalBalance;
        console.log("Account balance: " + this.accountBalance);
      });
  }

}

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'iban' })
export class Iban implements PipeTransform {
  transform(value: string): string {
    // remove existing spaces
    let lIban: string = value.replace(' ' , '');
    // place a space after every 4th character
    let result: string;
    result = lIban.charAt(0) + lIban.charAt(1) + ' ';
    lIban = lIban.substring(2);
    result = result + lIban.replace(/(.{4})/g, "$1 ");
    return result;
  }
}
