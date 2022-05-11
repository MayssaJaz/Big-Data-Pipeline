import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CryptohandlerService {

  constructor(private http: HttpClient) { }

  getCurrencies(): Observable<any> {
    let params = new HttpParams();
    return this.http.get("https://627be077b54fe6ee0090a637.mockapi.io/currencies");
  }
}
