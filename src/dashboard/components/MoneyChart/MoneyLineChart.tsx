import { LineChart } from '@mantine/charts';

export interface TransactionStatsProps {
  data: {
    date: string;
    expenses: number;
    income: number;
    total: number;
  }[];
}

const MoneyLineChart: React.FC<TransactionStatsProps> = ({ data }) => {
  const stats = data.map((stat) => {
    return {
      date: stat.date,
      expenses: stat.expenses,
      income: stat.income,
      total: stat.total,
    };
  });
  return (
    <LineChart
      h={400}
      data={stats}
      dataKey="date"
      series={[
        { name: 'expenses', label: 'Expenses', color: 'red.6' },
        { name: 'income', label: 'Income', color: 'green.6' },
        { name: 'total', label: 'Total', color: 'yellow.6' },
      ]}
      withLegend
      legendProps={{ verticalAlign: 'bottom', height: 50 }}
      curveType="monotone"
      style={{ minWidth: 200 }}
    />
  );
};

export default MoneyLineChart;
