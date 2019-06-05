import { Component, OnInit } from '@angular/core';
import * as SockJS from 'sockjs-client';
import {Stomp} from "@stomp/stompjs";
import {ApiService} from '../api.service';
import {Router} from '@angular/router';
import { ChartDataSets, ChartType, ChartOptions } from 'chart.js';
import { Label } from 'ng2-charts';
import {
  IncomeTransactionStats,
  OutcomeTransactionStats,
  RealTimeStatsTransactions
} from '../../dataModel/transaction';
import * as moment from 'moment';
import {ExchangeRateShort} from '../../dataModel/exchangeRate';

@Component({
  selector: 'app-transaction-stats',
  templateUrl: './transaction-stats.component.html',
  styleUrls: ['./transaction-stats.component.css']
})
export class TransactionStatsComponent implements OnInit {

  public typeOfStats: string = 'operationNumber';
  public realTimeCurrency: string = 'EUR';
  public showStats = false;
  private serverUrl = 'http://localhost:8080/finance';
  private stompClient;

  public barChartOptions: ChartOptions = {
    responsive: true,
    // We use these empty structures as placeholders for dynamic theming.
    scales: { xAxes: [{}], yAxes: [{}] },
    plugins: {
      datalabels: {
        anchor: 'end',
        align: 'end',
      }
    }
  };
  public barChartLabels: Label[] = [];
  public barChartType: ChartType = 'bar';
  public barChartLegend = true;

  public barChartData: ChartDataSets[] = [
    { data: [], label: 'Income transactions' },
    { data: [], label: 'Outcome transactions' }
  ];

  public barChartLabels1: Label[] = [];
  public barChartType1: ChartType = 'bar';
  public barChartLegend1 = true;

  public barChartData1: ChartDataSets[] = [
    { data: [], label: 'Income transactions' },
    { data: [], label: 'Outcome transactions' }
  ];


  minDate = new Date(2018, 0, 1);
  maxDate = new Date();

  public radarChartOptions: ChartOptions = {
    responsive: true,
    tooltips: {
      enabled: true,
      mode: 'single',
      callbacks: {
        label: function(tooltipItems, data) {
          return 'Value: ' + tooltipItems.yLabel;
        }
      }
    },
  };
  public radarChartLabels: Label[] = [];

  private incomeData: IncomeTransactionStats[] = [];
  private outcomeData: OutcomeTransactionStats[] = [];
  private incomeDataAmount: IncomeTransactionStats[] = [];
  private outcomeDataAmount: OutcomeTransactionStats[] = [];
  statsDate: Date;

  public radarChartData: ChartDataSets[] = [
    { data: [], label: 'Income transactions' },
    { data: [], label: 'Outcome transactions' }
  ];
  public radarChartType: ChartType = 'radar';

  constructor(private apiService: ApiService, router: Router) {
    if (!apiService.isLoggedIn()) {
      router.navigate(['/login']);
    }
    this.initializeConnection();
  }

  initializeConnection() {
    var socket = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(socket);
    let that = this;
    this.stompClient.connect({}, function (frame) {
      // console.log('Connected: ' + frame);
      that.stompClient.subscribe('/topic/transactions', function (greeting) {
          let realTimeStats: RealTimeStatsTransactions;
          realTimeStats = JSON.parse(greeting.body);
          // console.log(realTimeStats);
          if(that.realTimeCurrency == realTimeStats.currency) {
              let incomeData: number[] = that.barChartData[0].data as number[];
              let incomeDataAmount: number[] = that.barChartData1[0].data as number[];
              let outcomeData: number[] = that.barChartData[1].data as number[];
              let outcomeDataAmount: number[] = that.barChartData1[1].data as number[];
                incomeData.push(realTimeStats.incomeTransactions);
                incomeDataAmount.push(+realTimeStats.incomeSumAmount.toFixed(2));
                outcomeData.push(realTimeStats.outcomeTransactions);
                outcomeDataAmount.push(+realTimeStats.outcomeSumAmount.toFixed(2));
                let labelTime: string = moment(realTimeStats.dateEnd).format('hh:mm:ss:ms');
                that.barChartLabels.push(labelTime);
                that.barChartLabels1.push(labelTime);
                if(incomeData.length > 100) {
                  incomeData.splice(0,50);
                  incomeDataAmount.splice(0,50);
                }
                if(outcomeData.length > 100) {
                  outcomeData.splice(0,50);
                  outcomeDataAmount.splice(0, 50);
                  that.barChartLabels.splice(0, 50);
                }
                that.barChartData[0].data = incomeData;
                that.barChartData1[0].data = incomeDataAmount;
                that.barChartData[1].data = outcomeData;
                that.barChartData1[1].data = outcomeDataAmount;
          }
      });
    });
  }

  ngOnInit() {
  }

  // events
  public chartClicked({ event, active }: { event: MouseEvent, active: {}[] }): void {
    console.log(event, active);
  }

  public chartHovered({ event, active }: { event: MouseEvent, active: {}[] }): void {
    console.log(event, active);
  }

  public findRealTimeStats() {
    this.barChartLabels = [];
    this.barChartLabels1 = [];
    this.barChartData[0].data = [];
    this.barChartData1[0].data = [];
    this.barChartData[1].data = [];
    this.barChartData1[1].data = [];
    this.showStats = true;
  }

  public findStats() {
    let date: string = moment(this.statsDate).format('YY-MM-DD');
    this.incomeData = [];
    this.outcomeData = [];
    this.apiService.getTransactionsIncomeStats(date)
      .subscribe(response => {
        response.forEach(el => {
          this.incomeData.push(el);
        });
        let incomeTab: number[] = [];
        this.radarChartLabels = this.incomeData.map(x => x.toCurrency);
        if(this.typeOfStats == 'operationNumber'){
          incomeTab = this.incomeData.map(x => x.numberOfOperations);
        }
        else {
          incomeTab = this.incomeData.map(x => x.totalAmount);
        }
        this.radarChartData[0].data = incomeTab;
      });
    this.apiService.getTransactionsOutcomeStats(date)
      .subscribe(response => {
        response.forEach(el => {
          this.outcomeData.push(el);
        });
        let outcomeTab: number[] = [];
        if(this.typeOfStats == 'operationNumber') {
          outcomeTab = this.outcomeData .map(x => x.numberOfOperations);
        }
        else {
          outcomeTab = this.outcomeData .map(x => x.totalAmount);
        }
        // this.radarChartLabels = this.outcomeData.map(x => x.fromCurrency);
        this.radarChartData[1].data = outcomeTab;
      });
  }
}
