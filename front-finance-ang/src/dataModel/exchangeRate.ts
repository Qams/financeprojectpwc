export class ExchangeRateStats {
  minbid: number;
  maxbid: number;
  maxask: number;
  minask: number;
  changes: number;
  day: string;
  exchange: string;
  maxaskdate: number;
  maxbiddate: number;
  minaskdate: number;
  minbiddate: number;
  openAsk: number;
  openBid: number;
  closeAsk: number;
  closeBid: number;
}

export class ExchangeRate {
  openAsk: number;
  openBid: number;
  exchange: string;
  currencytime: number;
  ask: number;
  bid: number;
  askvolume: number;
  bidvolume: number;
  fromcurrency: string;
  tocurrency: string;
  id: string;
}

export class ExchangeRateShort {
  id: string;
  exchange: string;
  ask: number;
  bid: number;
}
