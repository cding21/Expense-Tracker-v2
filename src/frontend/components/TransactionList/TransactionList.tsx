import { Stack } from '@mantine/core';
import { mockTransactionList } from '@/mockTransaction';
import { TransactionListItem } from './TransactionListItem';

export function TransactionList() {
  const data = mockTransactionList;

  return (
    <Stack my="xl">
      {data.map((transaction) => (
        <TransactionListItem key={transaction.userId} transaction={transaction} />
      ))}
    </Stack>
  );
}
