'use client';

import React from 'react';
import { Text, Container, Divider, Grid, Space, Group, Paper, LoadingOverlay, Box } from '@mantine/core';
import { mockChartDataByMonth, mockTransactionStats } from '@/mockTransaction';
import CashFlowGrid from '../CashFlowGrid/CashFlowGrid';
import MoneyLineChart from '../MoneyLineChart/MoneyLineChart';
import classes from './Dashboard.module.css';
import { useQuery } from '@tanstack/react-query';
import { getTransactionSummary } from '@/helper/stats';

export function Dashboard() {
  const tmpData = [
    {
      id: '1',
      title: 'Money In',
      diff: 0,
      icon: 'pigMoney',
      value: 0,
    },
    {
      id: '2',
      title: 'Money Out',
      diff: 0,
      icon: 'cash',
      value: 0,
    },
    {
      id: '3',
      title: 'Net Change',
      diff: 0,
      icon: 'report',
      value: 0,
    },
  ];

  const {
    data: fetchedTransactionSummary = tmpData,
    isError: isLoadingTransactionsError,
    isFetching: isFetchingTransactions,
    isLoading: isLoadingTransactions,
  } = useQuery({
    queryKey: ['Transactions'],
    queryFn: async () => getTransactionSummary(),
    refetchOnWindowFocus: false,
  })


  return (
    <Container size={1200}>
      <div style={{ position: 'relative' }}>
        <LoadingOverlay
            visible={isFetchingTransactions}
            zIndex={1000}
            overlayProps={{ radius: 'sm', blur: 2 }}
        />
        <CashFlowGrid data={fetchedTransactionSummary} />
      </ div>
      <Divider mb={50} />
      <div>
        <Grid justify="center" align="stretch" grow gutter="lg">
          <Grid.Col span={4}>
            <Paper withBorder p="md" radius="md">
              <Group justify="space-between">
                <Text size="xs" c="dimmed" className={classes.title}>
                  Transaction Summary
                </Text>
                <Space h="lg" />
                <MoneyLineChart data={mockChartDataByMonth} />
              </Group>
            </Paper>
          </Grid.Col>
          <Grid.Col span={4}>
            <Paper withBorder p="md" radius="md">
              <Group justify="space-between">
                <Text size="xs" c="dimmed" className={classes.title}>
                  Recent Transactions
                </Text>
                <Space h="lg" />
                <MoneyLineChart data={mockChartDataByMonth} />
              </Group>
            </Paper>
          </Grid.Col>
        </Grid>
      </div>
    </Container>
  );
}
