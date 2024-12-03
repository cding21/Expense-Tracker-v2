'use client';

import React from 'react';
import {
  TextInput,
  PasswordInput,
  Paper,
  Title,
  Text,
  Container,
  Button,
  Anchor,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { useMutation } from '@tanstack/react-query';
import { notifications } from '@mantine/notifications';
import classes from './SignUp.module.css';
import { signUp } from '@/helper/auth';
import { UserLogin } from '@/models/user.model';
import { validatePassword, validateUsername } from '@/helper/validation';

export function SignUp() {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: { username: '', password: '', confirmPassword: '' },

    // functions will be used to validate values at corresponding key
    validate: {
      username: (value) => validateUsername(value),
      password: (value) => validatePassword(value),
      confirmPassword: (value, values) =>
        value !== values.password ? 'Passwords do not match' : validatePassword(value),
    },
  });

  const mutation = useMutation({
    mutationFn: (e: UserLogin) => signUp(e),
    onSuccess: () => {
     //Do nothing
    },
    onError: () => {
      notifications.show({
        message: `Sign-up failed`,
        color: 'red',
        position: 'bottom-center',
      });
    },
  });

  return (
    <Container size={420} my={40}>
      <Title ta="center" className={classes.title}>
        Create account
      </Title>
      <Text c="dimmed" size="sm" ta="center" mt="5">
        Already have an account?{' '}
        <Anchor
          size="sm"
          component="button"
          onClick={() => {
            window.location.href = '/sign-in';
          }}
        >
          Sign-in
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
          <PasswordInput
            name="confirm-password"
            label="Confirm password"
            placeholder="Your confirm password"
            required
            autoComplete="new-password"
            mt="md"
            key={form.key('confirmPassword')}
            {...form.getInputProps('confirmPassword')}
          />
          <Button name="Sign in" type="submit" fullWidth mt="xl" loading={mutation.isPending}>
            Sign up
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
