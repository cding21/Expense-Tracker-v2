'use client';

import React from 'react';
import { Title, Text, Container, Stack, Group, AppShell, Burger } from '@mantine/core';
import classes from './Dashboard.module.css';
import { ColorSchemeToggle } from '../ColorSchemeToggle/ColorSchemeToggle';
import TransactionList from '../TransactionList/TransactionList';
import { mockTransactionList, mockTransactionStats } from '@/mockTransaction';
import CashFlowGrid from '../CashFlowGrid/CashFlowGrid';
import { NavBar } from '../NavBar/NavBar';
import { useDisclosure } from '@mantine/hooks';
import { navbar } from '@nextui-org/react';

export function Dashboard() {
  const [opened, { toggle }] = useDisclosure();
  
  return (
    <AppShell
      header={{ 
        height: 30,
        collapsed: navbar.,
      }}
      navbar={{
        width: 80,
        breakpoint: 'sm',
        collapsed: { mobile: !opened },
      }}
      padding="md"
    >  
      <AppShell.Header>
        <Burger
          opened={opened}
          onClick={toggle}
          hiddenFrom="sm"
          size="sm"
        />
      </AppShell.Header>
      <AppShell.Navbar>
        <NavBar />
      </AppShell.Navbar>
      <AppShell.Main>
        <CashFlowGrid statistics={mockTransactionStats} />
        <TransactionList transactions={mockTransactionList} />
      </AppShell.Main>
    </AppShell>
  );
}
