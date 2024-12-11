'use client';

import React from 'react';
import {
  Title,
  Text,
  Container,
  AppShell,
  Burger,
  Divider,
  Grid,
  Card,
  Space,
  Group,
  Paper,
} from '@mantine/core';
import { mockChartDataByMonth, mockTransactionList, mockTransactionStats } from '@/mockTransaction';
import CashFlowGrid from '../CashFlowGrid/CashFlowGrid';
import MoneyChart from '../MoneyChart/MoneyChart';
import classes from './Dashboard.module.css';

export function Dashboard() {
  return (
    <Container size={1200}>
      <CashFlowGrid data={mockTransactionStats} />
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
                <MoneyChart data={mockChartDataByMonth} />
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
                <MoneyChart data={mockChartDataByMonth} />
              </Group>
            </Paper>
          </Grid.Col>
        </Grid>
      </div>
    </Container>
  );
}
