// 'use client';

import { Stack } from '@mantine/core';
import { TransactionListItem } from './TransactionListItem';
import { TransactionProps } from '../Transaction/Transaction';


export function TransactionList(transactions: TransactionProps[]) {
  return (
    <Stack my="xl">
      {transactions.map((transaction, index) => (
        <TransactionListItem key={index} {...transaction} />
      ))}
    </Stack>
  );
}
