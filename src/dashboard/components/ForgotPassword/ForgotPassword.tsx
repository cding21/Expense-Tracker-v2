'use client';

import {
  Paper,
  Title,
  Text,
  TextInput,
  Button,
  Container,
  Group,
  Anchor,
  Center,
  Box,
  rem,
} from '@mantine/core';
import { IconArrowLeft } from '@tabler/icons-react';
import classes from './ForgotPassword.module.css';

export function ForgotPassword() {
  return (
    <Container size={460} my={30}>
      <Title className={classes.title} ta="center">
        Forgot your password?
      </Title>
      <Text c="dimmed" fz="sm" ta="center" mt="5">
        Enter your email to get a reset link
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <TextInput label="Your email" placeholder="example@email.com" required />
        <Group justify="space-between" mt="lg" className={classes.controls}>
          <Anchor c="dimmed" size="sm" className={classes.control}>
            <Center inline>
              <IconArrowLeft style={{ width: rem(12), height: rem(12) }} stroke={1.5} />
              <Box
                ml={5}
                onClick={() => {
                  window.location.href = '/sign-in';
                }}
              >
                Back to the login page
              </Box>
            </Center>
          </Anchor>
          <Button className={classes.control} disabled>
            Reset password
          </Button>
        </Group>
      </Paper>
    </Container>
  );
}
