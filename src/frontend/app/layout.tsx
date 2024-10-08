import '@mantine/core/styles.css';
import React from 'react';
import { MantineProvider, ColorSchemeScript } from '@mantine/core';
import { theme } from '../theme';
import { Header } from '@/components/Header/Header';
import { Footer } from '@/components/Footer/Footer';
import Providers from './providers';

export const metadata = {
  title: 'Budget Buddy',
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
        <MantineProvider theme={theme}>
          <Providers>
            <Header />
            {children}
            <Footer />
          </Providers>
        </MantineProvider>
      </body>
    </html>
  );
}
