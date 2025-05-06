'use client';

import { Code, Text, Title } from '@mantine/core';
import { DataTable, useDataTableColumns } from 'mantine-datatable';
import { modals } from '@mantine/modals';
import { Transaction } from '@/models/transaction.model';

export interface TransactionListProps {
  transactions: {
    userId: string;
    date: string;
    amount: number;
    description: string;
    category: string;
    fromAccount: string;
    fromNote: string;
    toAccount: string;
    toNote: string;
  }[];
}

// eslint-disable-next-line arrow-body-style
const TransactionList: React.FC<TransactionListProps> = ({ transactions }) => {
  const key = 'draggable-example';

  const { effectiveColumns } = useDataTableColumns<Transaction>({
    key,
    columns: [
      { accessor: 'date', width: 10, draggable: false }, 
      { accessor: 'description', width: 40, draggable: true, ellipsis: true  }, 
      { accessor: 'category', width: 20,draggable: true },
      { accessor: 'amount', width: 10,draggable: true }, 
    ],
  });

  return (
    <>
      <Title order={2} ta='center'>Transactions</Title>
      <DataTable
        withColumnBorders
        minHeight={180}
        storeColumnsKey={key}
        columns={effectiveColumns}
        records={transactions}
        onRowClick={({ record, index }) => {
            modals.open({
              id: index.toString(),
              title: (<Title order={3}>Transaction Details</Title>),
              children: (
                <Text>
                  <Code>Date:</Code> {record.date} <br />
                  <Code>Amount:</Code> {record.amount} <br />
                  <Code>Category:</Code> {record.category} <br />
                  <Code>Description:</Code> {record.description} <br />
                  <Code>From Account:</Code> {record.fromAccount} <br />
                  <Code>From Note:</Code> {record.fromNote} <br />
                  <Code>To Account:</Code> {record.toAccount}  <br />
                  <Code>To Note:</Code> {record.toNote} <br />
                </Text>
              ),
              size: 'sm',
              centered: true,
          });
        }}
      />
    </>
  
  );
};

export default TransactionList;
