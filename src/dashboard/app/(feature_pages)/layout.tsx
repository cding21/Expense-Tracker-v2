'use client';

import '@mantine/charts/styles.css';
import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import React from 'react';
import { MantineProvider, ColorSchemeScript, AppShell, Burger, Grid, rem } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { Notifications } from '@mantine/notifications';
import { IconBrandMantine } from '@tabler/icons-react';
import { theme } from '../../theme';
import Providers from './providers';
import { NavBar } from '@/components/NavBar/NavBar';

// export const metadata = {
//   title: 'Budget Buddy Dashboard',
//   description: 'Your best buddy for budgeting and expense management!',
// };

export default function RootLayout({ children }: { children: any }) {
  const [opened, { toggle }] = useDisclosure();
  return (
    <html lang="en">
      <head>
        <ColorSchemeScript />
        <link rel="shortcut icon" href="/favicon.svg" />
        <meta
          name="viewport"
          content="minimum-scale=1, initial-scale=1, width=device-width, user-scalable=no"
        />
      </head>
      <body>
        <Providers>
          <MantineProvider theme={theme}>
            <Notifications />
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
              <AppShell.Main>{children}</AppShell.Main>
            </AppShell>
          </MantineProvider>
        </Providers>
      </body>
    </html>
  );
}
