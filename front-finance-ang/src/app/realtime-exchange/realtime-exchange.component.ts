import {Component, OnInit, ViewChild} from '@angular/core';
import * as SockJS from 'sockjs-client';
import {Stomp} from "@stomp/stompjs";
import {ApiService} from '../api.service';
import {Router} from '@angular/router';
import {ExchangeRate, ExchangeRateShort} from '../../dataModel/exchangeRate';
import {MatTableDataSource} from '@angular/material';
import {el} from '@angular/platform-browser/testing/src/browser_util';
import {ChartDataSets, ChartOptions} from 'chart.js';
import {BaseChartDirective, Label} from 'ng2-charts';
import * as moment from 'moment';

@Component({
  selector: 'app-realtime-exchange',
  templateUrl: './realtime-exchange.component.html',
  styleUrls: ['./realtime-exchange.component.css']
})
export class RealtimeExchangeComponent implements OnInit {

  private serverUrl = 'http://localhost:8080/hello';
  private stompClient;
  private exchangeRates: ExchangeRate[] = [];
  private openExchangeRates: ExchangeRateShort[] = [];
  dataSource = new MatTableDataSource<ExchangeRate>();
  displayedColumns: string[] = ['exchange', 'ask', 'bid', 'askchange', 'bidchange'];

  private activeExchange: string;


  public lineChartData: ChartDataSets[] = [
    { data: [], label: 'Ask' },
    { data: [], label: 'Bid' }
  ];
  public lineChartLabels: Label[] = [];

  public lineChartOptions: (ChartOptions & { annotation: any }) = {
    responsive: true,
    scales: {
      // We use this empty structure as a placeholder for dynamic theming.
      xAxes: [{}],
      yAxes: [
        {
          id: 'y-axis-0',
          position: 'left',
        }
      ]
    },
    annotation: {
      annotations: [
        {
          type: 'line',
          mode: 'vertical',
          scaleID: 'x-axis-0',
          value: 'March',
          borderColor: 'orange',
          borderWidth: 2,
          label: {
            enabled: true,
            fontColor: 'orange',
            content: 'LineAnno'
          }
        },
      ],
    },
  };
  public lineChartLegend = true;
  public lineChartType = 'line';
  // public lineChartPlugins = [pluginAnnotations];

  constructor(private apiService: ApiService, private router: Router) {
    if (!apiService.isLoggedIn()) {
      router.navigate(['/login']);
    }
    this.initializeConnection();
  }

  @ViewChild(BaseChartDirective) chart: BaseChartDirective;

  ngOnInit() {
    this.apiService.getCurrentExchangeRates()
      .subscribe(response => {
        response.forEach(element => this.exchangeRates.push(element));
        // console.log(this.exchangeRates);
        this.dataSource.data = this.exchangeRates;
      });

    this.apiService.getCurrentExchangeOpenRates()
      .subscribe(response => {
        response.forEach(element => this.openExchangeRates.push(element));
        // console.log(this.openExchangeRates);
      });
  }

  findStats() {
    this.lineChartLabels = [];
    this.lineChartData[0].data = [];
    this.lineChartData[1].data = [];
  }

  calcDiffBidOpen(exchange: string, val: number): string {
    let result: string = '0';
    this.openExchangeRates.forEach(x => {
        if(x.exchange == exchange) {
          result = (val - x.bid).toFixed(5);
          return result;
        }
    });
    return result;
  }

  calcDiffAskOpen(exchange: string, val: number): string {
    let result: string = '0';
    this.openExchangeRates.forEach(x => {
      if(x.exchange == exchange) {
        result = (val - x.ask).toFixed(5);
        // return result;
      }
    });
    return result;
  }

  isAskLowerThanOpen(exchange: string, val: number) {
    let result: boolean = false;
    this.openExchangeRates.forEach(x => {
      if(x.exchange == exchange && val < x.ask) {
        result = true;
        // return result;
      }
    });
    return result;
  }

  isBidLowerThanOpen(exchange: string, val: number) {
    let result: boolean = false;
    this.openExchangeRates.forEach(x => {
      if(x.exchange == exchange && val < x.bid) {
        result = true;
        // return result;
      }
    });
    return result;
  }

  initializeConnection() {
    var socket = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(socket);
    let that = this;
    this.stompClient.connect({}, function (frame) {
      // setConnected(true);
      // console.log('Connected: ' + frame);
      that.stompClient.subscribe('/topic/greetings', function (greeting) {
        let tmpDataSource = that.dataSource.data;
        tmpDataSource.forEach(x => {
          let exchangeRateShort: ExchangeRateShort;
          exchangeRateShort = JSON.parse(greeting.body);
          console.log(exchangeRateShort.id + ", DATE: " +  new Date().getTime());
          // console.log("GREETINGS " + greeting.body);
          // console.log("CURRENT: " + x.exchange);
          // console.log("FROMWS: " + exchangeRateShort.exchange);
          if(x.exchange == exchangeRateShort.exchange) {
            x.ask = exchangeRateShort.ask;
            x.bid = exchangeRateShort.bid;
            that.dataSource.data = tmpDataSource;
            if(exchangeRateShort.exchange == that.activeExchange) {
              let askData: number[] = that.lineChartData[0].data as number[];
              let bidData: number[] = that.lineChartData[1].data as number[];
              askData.push(exchangeRateShort.ask);
              bidData.push(exchangeRateShort.bid);
              if(askData.length > 100) {
                askData.splice(0,50);
              }
              if(bidData.length > 100) {
                bidData.splice(0,50);
                that.lineChartLabels.splice(0, 50);
              }
              that.lineChartData[0].data = askData;
              that.lineChartData[1].data = bidData;
              let labelTime: string = moment(Date.now()).format('hh:mm:ss:ms');
              that.lineChartLabels.push(labelTime);
            }
          }
        })
      });
    });
  }

}
