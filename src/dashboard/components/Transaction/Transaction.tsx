import React from 'react';
import { Box, Card, Flex, Group, Text } from '@mantine/core';
import classes from './Transaction.module.css';

export type TransactionProps = {
  transaction: {
    userId: string;
    date: string;
    amount: number;
    description: string;
    category: string;
    fromAccount: string;
    fromNote: string;
    toAccount: string;
    toNote: string;
  };
};

const Transaction: React.FC<TransactionProps> = ({ transaction }) => {
  const { userId, date, amount, description, category, fromAccount, fromNote, toAccount, toNote } =
    transaction;
  return (
    <Box maw={400}>
      <Flex align="center" justify="center" bg="blue" className={classes.category}>
        <Text c="white" size="xs">
          {category}
        </Text>
      </Flex>
      <Card shadow="sm" radius="md">
        <Text>{userId}</Text>
        <Text>{date}</Text>
        <Text>{amount}</Text>
        <Text>{description}</Text>
        <Text>{category}</Text>
        <Group>
          <Text>{fromAccount}</Text>
          <Text>{toAccount}</Text>
        </Group>
        <Text>{fromNote}</Text>
        <Text>{toNote}</Text>
      </Card>
    </Box>
  );
};

export default Transaction;
