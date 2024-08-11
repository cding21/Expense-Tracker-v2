'use client';

import { Card, Flex, Modal, Stack, Text } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import Transaction, { TransactionProps } from '../Transaction/Transaction';

export function TransactionListItem( transaction: TransactionProps) {
  const { amount, date, toAccount } = transaction;
  const [opened, { open, close }] = useDisclosure();

  return (
    <>
      <Modal opened={opened} onClose={close} title="Transaction Details" centered radius="md">
        <Transaction {...transaction} />
      </Modal>

      <Card withBorder onClick={open}>
        <Flex justify="space-between" align="center">
          <Stack>
            <Text>{toAccount}</Text>
            <Text>{date}</Text>
          </Stack>
          <Text>{amount}</Text>
        </Flex>
      </Card>
    </>
  );
}
