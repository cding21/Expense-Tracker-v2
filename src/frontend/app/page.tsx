import { Container } from '@mantine/core';
import { Welcome } from '../components/Welcome/Welcome';
import { Header } from '@/components/Header/Header';
import { Footer } from '@/components/Footer/Footer';

export default function HomePage() {
  return (
    <Container>
      <Welcome />
    </Container>
  );
}
