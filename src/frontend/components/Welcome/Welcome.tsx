import { Title, Text, Anchor } from '@mantine/core';
import classes from './Welcome.module.css';

export function Welcome() {
  return (
    <>
      <Title className={classes.title} ta="center" mt={100}>
        Welcome to{' '}
        <Text
          inherit
          variant="gradient"
          component="span"
          gradient={{ from: 'lightblue', to: 'blue' }}
        >
          Expense Tracker
        </Text>
      </Title>
      <Text c="dimmed" ta="center" size="lg" maw={580} mx="auto" mt="xl">
        This starter Next.js project is a minimal setup for Charlie&apos;s Ultimate Expense Tracker
        v2.{' '}
        <Anchor
          href="https://github.com/cding21/Expense-Tracker-v2/compare/frontend-starter?diff=split&w="
          size="lg"
        >
          Click here
        </Anchor>{' '}
        to get see the project source code.
      </Text>
    </>
  );
}
