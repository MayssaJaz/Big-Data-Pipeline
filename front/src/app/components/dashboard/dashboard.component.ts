import { Component, OnInit } from '@angular/core';
import { EChartsOption } from 'echarts';
import { CryptoCurrencyDto } from 'src/app/dto/crypto.dto';
import { CryptohandlerService } from 'src/app/services/cryptohandler.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  constructor(private cryptoHandler: CryptohandlerService) {}
  currenciesDetails: any[] = [];
  view: any[] = [1500,500];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Currency';
  showYAxisLabel = true;
  yAxisLabel = 'Price';

  ngOnInit(): void {
    this.cryptoHandler.getCurrencies().subscribe((data) => {
      this.currenciesDetails = [];
      data.forEach((item: CryptoCurrencyDto) => {
        this.currenciesDetails.push({
          name: item.id,
          value: item.price,
        });
      });
    });

    setInterval(() => {
      this.cryptoHandler.getCurrencies().subscribe((data) => {
        this.currenciesDetails = [];
        data.forEach((item: CryptoCurrencyDto) => {
          this.currenciesDetails.push({
            name: item.id,
            value: item.price,
          });
        });
      });
    }, 30000);
  }
}
