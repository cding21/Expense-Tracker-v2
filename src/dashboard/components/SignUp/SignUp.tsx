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
import { QueryClient, QueryClientProvider, useMutation } from '@tanstack/react-query';
import classes from './SignUp.module.css';
import { signUp } from '@/auth';

// Create a client
const queryClient = new QueryClient();

export function SignUp() {
  return (
    <QueryClientProvider client={queryClient}>
      <SignUpComponent />
    </QueryClientProvider>
  );
}

export function SignUpComponent() {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: { username: '', password: '', confirmPassword: '' },

    // functions will be used to validate values at corresponding key
    validate: {
      username: (value) => (value.length < 2 ? 'Name must have at least 2 letters' : null),
      password: (value) => (value.length < 6 ? 'Password must have at least 6 characters' : null),
      confirmPassword: (value, values) =>
        value !== values.password ? 'Passwords do not match' : null,
    },
  });

  type SignUpData = {
    username: string;
    password: string;
  };

  const mutation = useMutation({
    mutationFn: (e: SignUpData) => signUp(e),
    onSuccess: (data) => {
      console.log(data);
      // Redirect to sign-in page
      window.location.href = '/sign-in';
    },
    onError: (error) => {
      console.log(error);
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
          onSubmit={form.onSubmit((values) =>
            mutation.mutate({ username: values.username, password: values.password })
          )}
        >
          <TextInput
            name="username"
            label="Username"
            placeholder="Your username"
            required
            key={form.key('username')}
            {...form.getInputProps('username')}
          />
          <PasswordInput
            name="password"
            label="Password"
            placeholder="Your password"
            required
            mt="md"
            key={form.key('password')}
            {...form.getInputProps('password')}
          />
          <PasswordInput
            name="confirm-password"
            label="Confirm password"
            placeholder="Your confirm password"
            required
            mt="md"
            key={form.key('confirmPassword')}
            {...form.getInputProps('confirmPassword')}
          />
          {mutation.error && (
            <Text mt="xl" ta="center" color="red">
              {mutation.error.message}
            </Text>
          )}
          <Button name="Sign in" type="submit" fullWidth mt="xl" loading={mutation.isPending}>
            Sign up
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
