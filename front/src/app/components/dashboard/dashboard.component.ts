import { Component, OnInit } from '@angular/core';
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
  view: any[] = [1500, 500];

  // options
  currency:CryptoCurrencyDto|undefined;
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Currency';
  showYAxisLabel = true;
  yAxisLabel = 'Price';

  ngOnInit(): void {
    

    setInterval(() => {
      this.cryptoHandler.getCurrencies().subscribe((data) => {
        this.currenciesDetails = [];
       data.message.forEach((item: string) => {
          this.currency= JSON.parse(item)
          if (this.currency)
          this.currenciesDetails.push({
            name: this.currency.id,
            value: this.currency.price,
          });
        });
      });
    }, 30000);
  }
}
