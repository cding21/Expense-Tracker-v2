'use client';

import React, { useState } from 'react';
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
import classes from './SignIn.module.css';
import { login } from '@/auth';

export function SignIn() {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [error, setError] = useState(false);

  const handleSubmit = async () => {
    try {
      await login(formData);
      setError(false);
      window.location.href = '/';
    } catch (e) {
      if (e instanceof Error) {
        // Display an error message to the user
        setError(true);
      }
    }
  };

  const handleChange = (e: { target: { name: any; value: any } }) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

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
        <TextInput
          name="username"
          label="Username"
          placeholder="Your username"
          required
          onChange={handleChange}
          error={error}
        />
        <PasswordInput
          name="password"
          label="Password"
          placeholder="Your password"
          required
          mt="md"
          onChange={handleChange}
          error={error}
        />
        <Group justify="space-between" mt="lg">
          <Checkbox label="Remember me" />
          <Anchor
            component="button"
            size="sm"
            onClick={() => {
              window.location.href = '/forgot-password';
            }}
          >
            Forgot password?
          </Anchor>
        </Group>
        {error && (
          <Text c="red" mt="md" ta="center">
            Invalid username/password
          </Text>
        )}
        <Button name="Sign in" fullWidth mt="xl" onClick={() => handleSubmit()}>
          Sign in
        </Button>
      </Paper>
    </Container>
  );
}
