import { Title, Anchor, Text } from '@mantine/core';
import classes from './Welcome.module.css';

export function Welcome() {
  return (
    <>
      <Title className={classes.title} ta="center" mt={200}>
        Welcome to{' '}<br></br>
        <Text
          inherit
          variant="gradient"
          component="span"
          gradient={{ from: 'lightblue', to: 'blue' }}
        >
          Budget Buddy
        </Text>
      </Title>
      <Text c="dimmed" ta="center" size="lg" maw={580} mx="auto" mt="xl" mb={250}>
        <Anchor href="https://github.com/cding21/Expense-Tracker-v2" size="lg">
          Click here
        </Anchor>{' '}
        to get see the project source code.
      </Text>
    </>
  );
}
