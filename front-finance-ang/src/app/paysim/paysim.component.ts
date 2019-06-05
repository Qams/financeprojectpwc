import {Component, OnInit, ViewChild} from '@angular/core';
import {ApiService} from '../api.service';
import {FraudPaysimTransactions} from '../../dataModel/transaction';
import {MatPaginator, MatSort, MatTableDataSource} from '@angular/material';

@Component({
  selector: 'app-paysim',
  templateUrl: './paysim.component.html',
  styleUrls: ['./paysim.component.css']
})
export class PaysimComponent implements OnInit {

  fraudTransactions: FraudPaysimTransactions[] = [];
  dataSource = new MatTableDataSource<FraudPaysimTransactions>();
  displayedColumns: string[] = ['id', 'amount', 'probability'];

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private apiService: ApiService) { }

  ngOnInit() {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.apiService.getPaysimFraud()
      .subscribe(response => {
        response.forEach(element => this.fraudTransactions.push(element));
        console.log(this.fraudTransactions);
        this.dataSource.data = this.fraudTransactions;
      });
  }

}
