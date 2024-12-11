export const mockTransaction = {
  userId: 'cding',
  date: '15/05/2023',
  amount: 75.5,
  description: 'Grocery shopping',
  category: 'Groceries',
  fromAccount: 'Checking Account',
  fromNote: 'Weekly grocery shopping at the local supermarket',
  toAccount: 'Grocery Store',
  toNote: 'Payment for groceries',
};

export const mockTransactionList = [
  {
    userId: 'user123',
    date: '15/05/2023',
    amount: 75.5,
    description: 'Grocery shopping',
    category: 'Groceries',
    fromAccount: 'Checking Account',
    fromNote: 'Weekly grocery shopping at the local supermarket',
    toAccount: 'Grocery Store',
    toNote: 'Payment for groceries',
  },
  {
    userId: 'user124',
    date: '16/05/2023',
    amount: 150.0,
    description: 'Electricity bill',
    category: 'Utilities',
    fromAccount: 'Savings Account',
    fromNote: 'Monthly electricity bill',
    toAccount: 'Utility Company',
    toNote: 'Payment for electricity bill',
  },
  {
    userId: 'user125',
    date: '17/05/2023',
    amount: 45.0,
    description: 'Restaurant dinner',
    category: 'Food & Dining',
    fromAccount: 'Credit Card',
    fromNote: 'Dinner at local restaurant',
    toAccount: 'Restaurant',
    toNote: 'Payment for dinner',
  },
  {
    userId: 'user126',
    date: '18/05/2023',
    amount: 20.0,
    description: 'Public transportation',
    category: 'Transport',
    fromAccount: 'Checking Account',
    fromNote: 'Daily commute',
    toAccount: 'Transport Service',
    toNote: 'Payment for public transport',
  },
  {
    userId: 'user127',
    date: '19/05/2023',
    amount: 100.0,
    description: 'Internet bill',
    category: 'Utilities',
    fromAccount: 'Savings Account',
    fromNote: 'Monthly internet bill',
    toAccount: 'ISP',
    toNote: 'Payment for internet service',
  },
];

export const mockTransactionStats = [
  {
    title: 'Money In',
    diff: 25,
    icon: 'pigMoney',
    value: 1357.5,
  },
  {
    title: 'Money Out',
    diff: -4,
    icon: 'cash',
    value: -5120.39,
  },
  {
    title: 'Net Change',
    diff: 0,
    icon: 'report',
    value: -3762.89,
  },
];

export const mockChartDataByMonth = [
  {
    date: 'Mar 24',
    expenses: 2890,
    income: 2338,
    total: 2452,
  },
  {
    date: 'Apr 24',
    expenses: 2756,
    income: 2103,
    total: 2402,
  },
  {
    date: 'May 24',
    expenses: 3322,
    income: 986,
    total: 1821,
  },
  {
    date: 'Jun 24',
    expenses: 3470,
    income: 2108,
    total: 2809,
  },
  {
    date: 'Jul 24',
    expenses: 3129,
    income: 1726,
    total: 2290,
  },
];
