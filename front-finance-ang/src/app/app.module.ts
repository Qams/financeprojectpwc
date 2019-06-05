import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatMenuModule} from '@angular/material/menu';
import {
  MatBadgeModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule, MatDatepickerModule,
  MatDialogModule,
  MatExpansionModule,
  MatGridListModule,
  MatIconModule,
  MatInputModule, MatListModule, MatNativeDateModule,
  MatPaginatorModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatSelectModule,
  MatSnackBarModule, MatSortModule,
  MatTableModule,
  MatToolbarModule,
  MatTooltipModule
} from '@angular/material';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginComponent} from './login/login.component';
import {HttpClientModule} from '@angular/common/http';
import { DashboardComponent } from './dashboard/dashboard.component';
import {Iban} from './dashboard/dashboard.component';
import { AvatarModule } from 'ngx-avatar';
import { TransferComponent } from './transfer/transfer.component';
import {NgxPaginationModule} from 'ngx-pagination';
import { HistoricExchangeComponent } from './historic-exchange/historic-exchange.component';
import {ChartsModule} from "ng2-charts";
import { PaysimComponent } from './paysim/paysim.component';
import { RealtimeExchangeComponent } from './realtime-exchange/realtime-exchange.component';
import { TransactionStatsComponent } from './transaction-stats/transaction-stats.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    Iban,
    TransferComponent,
    HistoricExchangeComponent,
    PaysimComponent,
    RealtimeExchangeComponent,
    TransactionStatsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    MatIconModule,
    MatToolbarModule,
    MatBadgeModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatGridListModule,
    MatExpansionModule,
    MatProgressBarModule,
    MatPaginatorModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatSelectModule,
    MatTableModule,
    HttpClientModule,
    MatTooltipModule,
    ReactiveFormsModule,
    MatMenuModule,
    AvatarModule,
    NgxPaginationModule,
    ChartsModule,
    MatListModule,
    MatDatepickerModule,
    MatSortModule,
    MatPaginatorModule,
    MatNativeDateModule,        // <----- import for date formating(optional)
    MatButtonToggleModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
