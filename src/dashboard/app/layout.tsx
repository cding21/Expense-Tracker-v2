import '@mantine/core/styles.css';
import '@mantine/core/styles.layer.css';
import '@mantine/notifications/styles.css';
import 'mantine-datatable/styles.layer.css';
import React from 'react';
import { MantineProvider, ColorSchemeScript, Modal } from '@mantine/core';
import { Notifications } from '@mantine/notifications';
import { theme } from '../theme';
import Providers from './providers';
import { ModalsProvider } from '@mantine/modals';
import { AppShell } from '@mantine/core';
import { NavBar } from '@/components/NavBar/NavBar';
import { useDisclosure } from '@mantine/hooks';

export const metadata = {
  title: 'Budget Buddy Dashboard',
  description: 'Your best buddy for budgeting and expense management!',
};

export default function RootLayout({ children }: { children: any }) {  
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
            <ModalsProvider>
              <Notifications />
              {children}
            </ModalsProvider>
          </MantineProvider>
        </Providers>
      </body>
    </html>
  );
}
