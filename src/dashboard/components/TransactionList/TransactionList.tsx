// 'use client';

import { Stack } from '@mantine/core';
import { mockTransactionList } from '@/mockTransaction';
import { TransactionListItem } from './TransactionListItem';
// import { useEffect, useState } from 'react';

export function TransactionList() {
  // TODO: Once integration with the backend is complete, uncomment the following code:
  // const [data, setData] = useState([]);

  // useEffect(() => {
  //   const token = sessionStorage.getItem('token');
  //   fetch('/api/v1/transactions', {
  //     headers: {
  //     Authorization: `Bearer ${token}`
  //     }
  //   })
  //     .then((response) => response.json())
  //     .then((responseData) => setData(responseData))
  //     .catch((error) => console.error(error));
  // }, []);

  const data = mockTransactionList;

  return (
    <Stack my="xl">
      {data.map((transaction, index) => (
        <TransactionListItem key={index} transaction={transaction} />
      ))}
    </Stack>
  );
}
