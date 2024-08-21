'use client';

import React from 'react';
import { Title, Text, Container } from '@mantine/core';
import classes from './Dashboard.module.css';
import { ColorSchemeToggle } from '../ColorSchemeToggle/ColorSchemeToggle';
import TransactionList from '../TransactionList/TransactionList';
import { mockTransactionList, mockTransactionStats } from '@/mockTransaction';
import { SignOut } from '../SignOut/SignOut';
import CashFlowGrid from '../CashFlowGrid/CashFlowGrid';

export function Dashboard() {
  return (
    <Container size={800} my={40}>
      <Title ta="center" className={classes.title}>
        Dashboard
      </Title>
      <Text ta="center">Welcome to your dashboard</Text>
      <Text ta="center">More to come soon...</Text>
      <CashFlowGrid statistics={mockTransactionStats} />
      <TransactionList transactions={mockTransactionList} />
      <SignOut />
      <ColorSchemeToggle />
    </Container>
  );
}
