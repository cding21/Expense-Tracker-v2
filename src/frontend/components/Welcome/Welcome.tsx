import classes from './Welcome.module.css';
import { AppShell, Group, Burger, Skeleton, Title, Anchor, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { MantineLogo } from '@mantinex/mantine-logo';

export function Welcome() {
  const [opened, { toggle }] = useDisclosure();

  return (   
    <>
      <AppShell
        header={{ height: 60 }}
        navbar={{ width: 300, breakpoint: 'sm', collapsed: { mobile: !opened } }}
        padding="md"
      >
        <AppShell.Header>
          <Group h="100%" px="md">
            <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
            <MantineLogo size={30} />
          </Group>
        </AppShell.Header>
        <AppShell.Navbar p="md">
          Navbar
          {Array(15)
            .fill(0)
            .map((_, index) => (
              <Skeleton key={index} h={28} mt="sm" animate={false} />
            ))}
        </AppShell.Navbar>
        <AppShell.Main>
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
            <Anchor href="https://github.com/cding21/Expense-Tracker-v2" size="lg">
              Click here
            </Anchor>{' '}
            to get see the project source code.
          </Text>
        </AppShell.Main>
      </AppShell>
    </>
  );
}
