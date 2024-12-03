'use client';

import React from 'react';
import {
  TextInput,
  PasswordInput,
  Checkbox,
  Anchor,
  Paper,
  Title,
  Text,
  Container,
  Group,
  Button,
} from '@mantine/core';
import { useMutation } from '@tanstack/react-query';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import classes from './SignIn.module.css';
import { login } from '@/helper/auth';
import { UserLogin } from '@/models/user.model';

export function SignIn() {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: { username: '', password: '' },

    // functions will be used to validate values at corresponding key
    validate: {
      username: (value) => (value.length > 0 ? null : 'Username is required'),
      password: (value) => (value.length > 0 ? null : 'Password is required'),
    },
  });

  const mutation = useMutation({
    mutationFn: (e: UserLogin) => login(e),
    onSuccess: () => {
      // Do nothing
    },
    onError: () => {
      notifications.show({
        message: 'Login failed: Invalid username/password',
        color: 'red',
        position: 'bottom-center',
      });
    },
  });

  return (
    <Container size={420} my={40}>
      <Title ta="center" className={classes.title}>
        Welcome back!
      </Title>
      <Text c="dimmed" size="sm" ta="center" mt={5}>
        Do not have an account yet?{' '}
        <Anchor
          size="sm"
          component="button"
          onClick={() => {
            window.location.href = '/sign-up';
          }}
        >
          Create account
        </Anchor>
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <form
          onSubmit={form.onSubmit((values) => {
            mutation.mutate({ username: values.username, password: values.password });
          })}
          autoComplete="off"
        >
          <TextInput
            name="username"
            label="Username"
            placeholder="Your username"
            required
            autoComplete="new-username"
            key={form.key('username')}
            {...form.getInputProps('username')}
          />
          <PasswordInput
            name="password"
            label="Password"
            placeholder="Your password"
            required
            autoComplete="new-password"
            mt="md"
            key={form.key('password')}
            {...form.getInputProps('password')}
          />
          <Group justify="space-between" mt="lg">
            <Checkbox label="Remember me" />
            <Anchor
              component="a"
              size="sm"
              onClick={() => {
                window.location.href = '/forgot-password';
              }}
            >
              Forgot password?
            </Anchor>
          </Group>
          <Button name="Sign in" type="submit" fullWidth mt="xl" loading={mutation.isPending}>
            Sign in
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
