import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { ChartOptions, ChartType, ChartDataSets } from 'chart.js';
// import * as pluginDataLabels from 'chartjs-plugin-datalabels';
import { Label } from 'ng2-charts';
import {ApiService} from '../api.service';
import {ExchangeRateStats} from '../../dataModel/exchangeRate';
import {MatTableDataSource} from '@angular/material';
import * as moment from 'moment';

@Component({
  selector: 'app-historic-exchange',
  templateUrl: './historic-exchange.component.html',
  styleUrls: ['./historic-exchange.component.css']
})
export class HistoricExchangeComponent implements OnInit {

  private stats: ExchangeRateStats[] = [];
  dataSource = new MatTableDataSource<ExchangeRateStats>();
  minDate = new Date(2018, 0, 1);
  maxDate = new Date();
  statsDate: Date;
  // private dataSource: PeriodicElement[] = [];

  displayedColumns: string[] = ['exchange', 'maxbid', 'minbid', 'maxask', 'minask', 'changes', 'open', 'close'];

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
  public barChartLabels: Label[] = ['2006', '2007', '2008', '2009', '2010', '2011', '2012'];
  public barChartType: ChartType = 'bar';
  public barChartLegend = true;
  // public barChartPlugins = [pluginDataLabels];

  public barChartData: ChartDataSets[] = [
    { data: [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], label: 'Ask Change' },
    { data: [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], label: 'Bid Change' }
  ];

  constructor(private apiService: ApiService) { }

  ngOnInit() {

  }

  // events
  public chartClicked({ event, active }: { event: MouseEvent, active: {}[] }): void {
    console.log(event, active);
  }

  public chartHovered({ event, active }: { event: MouseEvent, active: {}[] }): void {
    console.log(event, active);
  }

  public findStats() {
    this.stats = [];
    let date: string = moment(this.statsDate).format('YY-MM-DD');
    this.apiService.getExchangeRateStats(date)
      .subscribe(response => {
        // console.log(response);
        response.forEach(element => {
          this.stats.push(element);
        });
        this.barChartLabels = this.stats.map(x => x.exchange);
        const askTab: number[] = this.stats.map(x => +(x.closeAsk - x.openAsk).toPrecision(5));
        const bidTab: number[] = this.stats.map(x => +(x.closeBid - x.openBid).toPrecision(5));
        this.barChartData[0].data = askTab;
        this.barChartData[1].data = bidTab;
        this.dataSource.data = this.stats;
      });
  }

}
