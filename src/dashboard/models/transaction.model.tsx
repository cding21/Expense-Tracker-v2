export type Transaction = {
  id: string;
  userId: string;
  date: string;
  amount: number;
  currencyCode: string;
  description: string;
  category: string;
  tags: string[];
  fromAccount: string;
  fromNote: string;
  toAccount: string;
  toNote: string;
};

export type TransactionStats = {
  id: string;
  title: string;
  diff: number;
  icon: string;
  value: number;
};
