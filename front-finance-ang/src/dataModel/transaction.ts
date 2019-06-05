export class Transaction {
  id: string;
  ftowner: string;
  fttimestamp: number;
  amount: number;
  fromaccount: string;
  fromcurrency: string;
  ftamount: number;
  originalamount: number;
  receiver: string;
  sender: string;
  title: string;
  toaccount: string;
  tocurrency: string;
  type: string;
}

export class Transfer {
  fromAccount: string;
  toAccount: string;
  currency: string;
  amount: number;
  sender: string;
  receiver: string;
  title: string;
}

export class FraudPaysimTransactions {
  id: string;
  amount: number;
  probability: number;
}

export class IncomeTransactionStats {
  toCurrency: string;
  numberOfOperations: number;
  totalAmount: number;
}

export class OutcomeTransactionStats {
  fromCurrency: string;
  numberOfOperations: number;
  totalAmount: number;
}

export class RealTimeStatsTransactions {
  currency: string;
  incomeSumAmount: number;
  outcomeSumAmount: number;
  incomeTransactions: number;
  outcomeTransactions: number;
  dateStart: number;
  dateEnd: number;
}


