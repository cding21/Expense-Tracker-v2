// 'use client';

import { Stack } from '@mantine/core';
import { TransactionListItem } from './TransactionListItem';
import { TransactionProps } from '../Transaction/Transaction';

export interface TransactionListProps {
  transactions : TransactionProps[];
};

export function TransactionList({ transactions }: TransactionListProps) {
  return (
    <Stack my="xl">
      {transactions.map((transaction, index) => (
        <TransactionListItem key={index} transaction={transaction} />
      ))}
    </Stack>
  );
}
