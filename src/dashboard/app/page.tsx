'use client';

import { Dashboard } from '@/components/Dashboard/Dashboard';
import { NavBar } from '@/components/NavBar/NavBar';
import { AppShell, Burger, Grid, rem } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconBrandMantine } from '@tabler/icons-react';

export default function HomePage() {
  const [opened, { toggle }] = useDisclosure();

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{
        width: 300,
        breakpoint: 'sm',
        collapsed: { mobile: !opened },
      }}
      padding="md"
    >
      <AppShell.Header>
        <Grid>
          <Grid.Col
            span={4}
            style={{ display: 'flex', justifyContent: 'left', alignItems: 'center' }}
          >
            <Burger opened={opened} onClick={toggle} hiddenFrom="sm" size="sm" />
          </Grid.Col>
          <Grid.Col
            span={4}
            style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}
          >
            <IconBrandMantine
              style={{ width: rem(60), height: rem(60) }}
              stroke={1.5}
              color="var(--mantine-color-blue-filled)"
            />
          </Grid.Col>
          <Grid.Col span={4}></Grid.Col>
        </Grid>
      </AppShell.Header>
      <AppShell.Navbar>
        <NavBar />
      </AppShell.Navbar>
      <AppShell.Main>
        <Dashboard />
      </AppShell.Main>
    </AppShell>
  );
}
