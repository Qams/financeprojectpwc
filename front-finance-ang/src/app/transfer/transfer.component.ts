import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {ApiService} from '../api.service';
import {Transfer} from '../../dataModel/transaction';
import {showSnackbar} from '../../utils/frontComponents';
import {ErrorStateMatcher, MatSnackBar} from '@angular/material';
import {Router} from '@angular/router';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {

  matcher = new TransferStateMatcher();

  receiverFormControl = new FormControl('', [
    Validators.required
  ]);

  toAccountFormControl = new FormControl('', [
    Validators.required,
    Validators.maxLength(26)
  ]);

  titleFormControl = new FormControl('', [
    Validators.required
  ]);

  amountFormControl = new FormControl('', [
    Validators.required,
    Validators.min(0)
  ]);

  currency: string = '';

  constructor(private apiService: ApiService, private snackbar: MatSnackBar, private router: Router) {
    this.currency = this.apiService.getCurrentAccountCurrency();
    if (!apiService.isLoggedIn()) {
      router.navigate(['/login']);
    }
  }

  ngOnInit() {
  }

  transfer() {
    this.receiverFormControl.markAsTouched();
    this.toAccountFormControl.markAsTouched();
    this.titleFormControl.markAsTouched();
    this.amountFormControl.markAsTouched();
    if(this.isFormValid()) {
      let transfer = new Transfer();
      transfer.amount = this.amountFormControl.value;
      transfer.currency = this.currency;
      transfer.fromAccount = this.apiService.getCurrentAccountNumber();
      transfer.toAccount = this.toAccountFormControl.value.replace(/\s/g,'');
      transfer.receiver = this.receiverFormControl.value;
      transfer.sender = this.apiService.getFirstName() + ' ' + this.apiService.getSurname();
      transfer.title = this.titleFormControl.value;
      console.log(transfer);
      this.apiService.transferMoney(transfer).then(() => {
        showSnackbar(this.snackbar, 'Transfer has been sent');
        // this.router.navigate(['/dashboard']);
      })
        .catch(error => {
          console.log(error);
          showSnackbar(this.snackbar, 'Issues with transfer money (error ' + error.toString() + ')')
        }
        );

    }
  }

  private isFormValid() : boolean {
    return this.toAccountFormControl.valid
      && this.receiverFormControl.valid
      && this.titleFormControl.valid
      && this.amountFormControl.valid;
  }

}

export class TransferStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
