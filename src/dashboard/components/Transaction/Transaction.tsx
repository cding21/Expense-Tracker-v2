import React from 'react';
import { Box, Card, Flex, Group, Text } from '@mantine/core';
import classes from './Transaction.module.css';
import { FaArrowRight } from 'react-icons/fa';

export interface TransactionProps {
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

const Transaction: React.FC<TransactionProps> = (transaction) => {
  const { date, amount, description, category, fromAccount, fromNote, toAccount, toNote } =
    transaction;
  return (
    <Box maw={450}>
      <Flex align="center" justify="center" bg="blue" className={classes.category}>
        <Text c="white" size="xs">
          {category}
        </Text>
      </Flex>
      <Card shadow="sm" radius="md">
        <Group>
          <Text fw={500}>Date:</Text>
          <Text>{date}</Text>
        </Group>
        <Group>
          <Text fw={500}>Amount:</Text>
          <Text>{amount}</Text>
        </Group>
        <Group>
          <Text fw={500}>Description:</Text>
          <Text>{description}</Text>
        </Group>
        <Group>
          <Text fw={500}>Accounts:</Text>
          <Text>{fromAccount}</Text>
          <FaArrowRight />
          <Text>{toAccount}</Text>
        </Group>
        <Group>
          <Text fw={500}>Note:</Text>
          <Text >{fromNote}</Text>
        </Group>
      </Card>
    </Box>
  );
};

export default Transaction;
