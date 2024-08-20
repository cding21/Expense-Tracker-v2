import { Group, Paper, SimpleGrid, Text } from '@mantine/core';
import {
  IconUserPlus,
  IconDiscount2,
  IconReceipt2,
  IconCoin,
  IconArrowUpRight,
  IconArrowDownRight,
  IconEqual,
  IconCash,
  IconPigMoney,
  IconReportMoney
} from '@tabler/icons-react';
import classes from './CashFlowGrid.module.css';
import { formatCurrency, formatPercentage } from '@/helper/formatter';

enum IconsKey {
  user = 'user',
  discount = 'discount',
  receipt = 'receipt',
  coin = 'coin',
  cash = 'cash',
  pig = 'pig',
  report = 'report',
}

const icons = {
  [IconsKey.user]: IconUserPlus,
  [IconsKey.discount]: IconDiscount2,
  [IconsKey.receipt]: IconReceipt2,
  [IconsKey.coin]: IconCoin,
  [IconsKey.cash]: IconCash,
  [IconsKey.pig]: IconPigMoney,
  [IconsKey.report]: IconReportMoney,
};

export interface TransactionStatsProps {
  statistics: {
    title: string;
    diff: number;
    icon: string;
    value: number;
  }[];
}

const CashFlowGrid: React.FC<TransactionStatsProps> = ({ statistics }) => {
  const stats = statistics.map((stat) => {
    const key = stat.icon as keyof typeof IconsKey;
    const Icon = icons[key] ? icons[key] : IconCoin;
    const DiffIcon =
      stat.diff > 0 ? IconArrowUpRight : stat.diff === 0 ? IconEqual : IconArrowDownRight;

    return (
      <Paper withBorder p="md" radius="md" key={stat.title}>
        <Group justify="space-between">
          <Text size="xs" c="dimmed" className={classes.title}>
            {stat.title}
          </Text>
          <Icon className={classes.icon} size="1.4rem" stroke={1.5} />
        </Group>

        <Group align="flex-end" gap="xs" mt={25}>
          <Text className={classes.value}>{formatCurrency(stat.value)}</Text>
          <Text
            c={stat.diff > 0 ? 'teal' : stat.diff === 0 ? 'yellow' : 'red'}
            fz="sm"
            fw={500}
            className={classes.diff}
          >
            <span>{stat.diff >= 0? formatPercentage(stat.diff, {prefix: '+'}): formatPercentage(stat.diff)}</span>
            <DiffIcon size="1rem" stroke={1.5} />
          </Text>
        </Group>

        <Text fz="xs" c="dimmed" mt={7}>
          Compared to previous month
        </Text>
      </Paper>
    );
  });
  return (
    <div className={classes.root}>
      <SimpleGrid cols={{ base: 1, xs: 2, md: 3 }}>{stats}</SimpleGrid>
    </div>
  );
};

export default CashFlowGrid;
