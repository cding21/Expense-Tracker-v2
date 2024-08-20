import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import React from 'react';
import { MantineProvider, ColorSchemeScript } from '@mantine/core';
import { theme } from '../theme';
import Providers from './providers';
import { Notifications } from '@mantine/notifications';

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
              <Notifications /> 
              {children}
            </MantineProvider>
        </Providers>
      </body>
    </html>
  );
}
