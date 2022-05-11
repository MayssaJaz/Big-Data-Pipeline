import { TestBed } from '@angular/core/testing';

import { CryptohandlerService } from './cryptohandler.service';

describe('CryptohandlerService', () => {
  let service: CryptohandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CryptohandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
