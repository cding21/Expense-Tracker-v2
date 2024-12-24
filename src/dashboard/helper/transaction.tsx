'use server';

import { Transaction } from '@/models/transaction.model';
import { cookies } from 'next/headers';

export async function createTransaction(transaction: Transaction) {
  // Retrieve the token from the cookies
  const token = (await cookies()).get('token');

  // Make an API request to check if the username is available
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/transactions`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token!.value}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(transaction),
  });

  // Check if the creation was successful
  if (response.status === 201) {
    return true;
  } else {
    throw new Error('An error occurred');
  }
}

export async function getTransactions(): Promise<Transaction[]> {
  // Retrieve the token from the cookies
  const token = (await cookies()).get('token');

  // Make an API request to check if the username is available
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/transactions`, {
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
    throw new Error('An error occurred');
  }
}

export async function getTransaction(id: string): Promise<Transaction> {
  // Retrieve the token from the cookies
  const token = (await cookies()).get('token');

  // Make an API request to check if the username is available
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/transactions/${id}`, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token!.value}`,
      'Content-Type': 'application/json',
    },
  });

  // Check if the transaction is available
  if (response.status === 200) {
    return response.json();
  } else {
    throw new Error('An error occurred');
  }
}

export async function updateTransaction(transaction: Transaction) {
  // Retrieve the token from the cookies
  const token = (await cookies()).get('token');

  // Morph the transaction object to match the API
  const transactionPayload = {
    date: transaction.date,
    amount: transaction.amount,
    currencyCode: transaction.currencyCode,
    description: transaction.description,
    category: transaction.category,
    fromAccount: transaction.fromAccount,
    fromNote: transaction.fromNote,
    toAccount: transaction.toAccount,
    toNote: transaction.toNote,
  };

  // Make an API request to check if the username is available
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/transactions/${transaction.id}`, {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token!.value}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(transactionPayload),
  });

  // Check if the update was successful
  if (response.status === 200) {
    return true;
  } else {
    throw new Error('An error occurred');
  }
}

export async function deleteTransaction(id: string) {
  // Retrieve the token from the cookies
  const token = (await cookies()).get('token');

  // Make an API request to check if the username is available
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/transactions/${id}`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token!.value}`,
      'Content-Type': 'application/json',
    },
  });

  // Check if the deletion was successful
  if (response.status === 200) {
    return true;
  } else {
    throw new Error('An error occurred');
  }
}
