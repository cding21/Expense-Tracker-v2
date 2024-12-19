'use client';

import React from 'react';
import {
  Text,
  Container,
  Divider,
  Grid,
  Space,
  Group,
  Paper,
} from '@mantine/core';
import { mockChartDataByMonth, mockTransactionStats } from '@/mockTransaction';
import CashFlowGrid from '../CashFlowGrid/CashFlowGrid';
import MoneyLineChart from '../MoneyLineChart/MoneyLineChart';
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
