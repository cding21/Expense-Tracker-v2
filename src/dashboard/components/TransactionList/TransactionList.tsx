// 'use client';

import { Stack } from '@mantine/core';
import { TransactionListItem } from './TransactionListItem';
import { TransactionProps } from '../Transaction/Transaction';

export interface TransactionListProps {
  transactions : TransactionProps[];
};

export function TransactionList(transactionsProps: TransactionListProps) {
  return (
    <Stack my="xl">
      {transactionsProps.transactions.map((transaction, index) => (
        <TransactionListItem key={index} {...transaction} />
      ))}
    </Stack>
  );
}
