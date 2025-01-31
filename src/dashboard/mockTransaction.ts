import { Transaction } from './models/transaction.model';

export const mockTransaction: Transaction = {
  id: '1',
  userId: 'cding',
  date: '15/05/2023',
  amount: 75.5,
  currencyCode: 'AUD',
  description: 'Grocery shopping',
  category: 'Groceries',
  tags: ['food', 'groceries'],
  fromAccount: 'Checking Account',
  fromNote: 'Weekly grocery shopping at the local supermarket',
  toAccount: 'Grocery Store',
  toNote: 'Payment for groceries',
};

export const mockTransactionList: Transaction[] = [
  {
    id: '1',
    userId: 'user123',
    date: '15/05/2023',
    amount: 75,
    currencyCode: 'AUD',
    description: 'Grocery shopping',
    category: 'Groceries',
    tags: ['food', 'groceries'],
    fromAccount: 'Checking Account',
    fromNote: 'Weekly grocery shopping at the local supermarket',
    toAccount: 'Grocery Store',
    toNote: 'Payment for groceries',
  },
  {
    id: '2',
    userId: 'user124',
    date: '16/05/2023',
    amount: 150,
    currencyCode: 'AUD',
    description: 'Electricity bill',
    category: 'Utilities',
    tags: ['electricity', 'bills'],
    fromAccount: 'Savings Account',
    fromNote: 'Monthly electricity bill',
    toAccount: 'Utility Company',
    toNote: 'Payment for electricity bill',
  },
  {
    id: '3',
    userId: 'user125',
    date: '17/05/2023',
    amount: 45,
    currencyCode: 'AUD',
    description: 'Restaurant dinner',
    category: 'Food & Dining',
    tags: ['food', 'dining'],
    fromAccount: 'Credit Card',
    fromNote: 'Dinner at local restaurant',
    toAccount: 'Restaurant',
    toNote: 'Payment for dinner',
  },
  {
    id: '4',
    userId: 'user126',
    date: '18/05/2023',
    amount: 20,
    currencyCode: 'AUD',
    description: 'Public transportation',
    category: 'Transport',
    tags: ['transport', 'commute'],
    fromAccount: 'Checking Account',
    fromNote: 'Daily commute',
    toAccount: 'Transport Service',
    toNote: 'Payment for public transport',
  },
  {
    id: '5',
    userId: 'user127',
    date: '19/05/2023',
    amount: 100,
    currencyCode: 'AUD',
    description: 'Internet bill',
    category: 'Utilities',
    tags: ['internet', 'bills'],
    fromAccount: 'Savings Account',
    fromNote: 'Monthly internet bill',
    toAccount: 'ISP',
    toNote: 'Payment for internet service',
  },
];

export const mockTransactionStats = [
  {
    id: '1',
    title: 'Money In',
    diff: 25,
    icon: 'pigMoney',
    value: 1357.5,
  },
  {
    id: '2',
    title: 'Money Out',
    diff: -4,
    icon: 'cash',
    value: -5120.39,
  },
  {
    id: '3',
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

export const mockTransactionCategories = [
  'Groceries',
  'Utilities',
  'Food & Dining',
  'Transport',
  'Internet',
  'Entertainment',
  'Health & Fitness',
  'Personal Care',
  'Education',
  'Gifts & Donations',
  'Investments',
  'Insurance',
  'Taxes',
  'Business',
  'Travel',
  'Shopping',
  'Home',
  'Pets',
  'Kids',
  'Miscellaneous',
];

export const mockTransactionCurrencyCodes = [
  'AUD',
  'USD',
  'EUR',
  'GBP',
  'JPY',
  'CAD',
  'CHF',
  'CNY',
  'SEK',
  'NZD',
];
