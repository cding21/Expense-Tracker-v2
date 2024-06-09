import { Container, Title } from '@mantine/core';
import Transaction from '@/components/Transaction/Transaction';
import { mockTransaction } from '@/mockTransaction';
import { TransactionList } from '@/components/TransactionList/TransactionList';

export default function GetTransaction() {
  return (
    <Container>
      <Title>Transactions</Title>
      <Transaction transaction={mockTransaction} />
      <TransactionList />
    </Container>
  );
}
