'use server';

import { TransactionStats } from '@/models/transaction.model';
import { cookies } from 'next/headers';

export async function getTransactionSummary(): Promise<TransactionStats[]> {
  // Retrieve the token from the cookies
  const token = (await cookies()).get('token');

  // Make an API request to check if the username is available
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/transactions/summary`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token!.value}`,
      'Content-Type': 'application/json',
    },
  });

  // Check if the transactions are available
  if (response.status === 200) {
    return response.json();
  } else {
    throw new Error('An error occurred fetching transaction summary');
  }
}
