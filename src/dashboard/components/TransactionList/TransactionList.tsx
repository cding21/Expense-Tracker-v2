// 'use client';

import { Stack } from '@mantine/core';
import { TransactionListItem } from './TransactionListItem';

export interface TransactionListProps {
  transactions: {
    userId: string;
    date: string;
    amount: number;
    description: string;
    category: string;
    fromAccount: string;
    fromNote: string;
    toAccount: string;
    toNote: string;
  }[];
}

// eslint-disable-next-line arrow-body-style
const TransactionList: React.FC<TransactionListProps> = ({ transactions }) => {
  return (
    <Stack my="xl">
      {transactions.map((transaction, index) => (
        <TransactionListItem key={index} transaction={transaction} />
      ))}
    </Stack>
  );
};

export default TransactionList;
