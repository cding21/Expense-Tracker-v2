import { Container } from '@mantine/core';
import { TransactionList } from '@/components/TransactionList/TransactionList';

export default function HomePage() {
  return (
    <Container>
      <TransactionList />
    </Container>
  );
}
