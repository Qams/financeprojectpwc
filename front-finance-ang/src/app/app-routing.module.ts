import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {TransferComponent} from './transfer/transfer.component';
import {HistoricExchangeComponent} from './historic-exchange/historic-exchange.component';
import {PaysimComponent} from './paysim/paysim.component';
import {RealtimeExchangeComponent} from './realtime-exchange/realtime-exchange.component';
import {TransactionStatsComponent} from './transaction-stats/transaction-stats.component';

const routes: Routes = [
  // { path: '', redirectTo: 'login', pathMatch: 'full'},
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'transfer', component: TransferComponent },
  { path: 'paysim', component: PaysimComponent },
  { path: 'historic-exchange', component: HistoricExchangeComponent },
  { path: 'transaction-stats', component: TransactionStatsComponent },
  { path: 'realtime-exchange', component: RealtimeExchangeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
