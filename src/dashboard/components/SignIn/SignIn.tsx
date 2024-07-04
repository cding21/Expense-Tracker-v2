"use client"
import React, { useState } from "react";
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

export function SignIn() {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });
  const [error, setError] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(false);

    // Make an API request to authenticate the user
    const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
    try {
      const response = await fetch(`${backendUrl}/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });
      // Check if the login was successful
      if (response.ok) {
        // Redirect the user to the dashboard or home page
        window.location.href = '/dashboard';
      } else {
        // Display an error message to the user
        const errorData = await response.json();
        console.error('Login failed:', errorData.message);
        setError(true);
      }
    } catch {
      // Display an error message to the user
      console.error('Login failed');
      setError(true);
    }
  };

  const handleChange = (e) => {
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
        <Anchor size="sm" component="button" onClick={() => {
                window.location.href = '/sign-up';
              }}>
          Create account
        </Anchor>
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <TextInput name="username" label="Username" placeholder="Your username" required onChange={handleChange} error={error}/>
        <PasswordInput name="password" label="Password" placeholder="Your password" required mt="md" onChange={handleChange} error={error}/>
        <Group justify="space-between" mt="lg">
          <Checkbox label="Remember me" />
          <Anchor component="button" size="sm">
            Forgot password?
          </Anchor>
        </Group>
        {error && <Text c="red" mt="md" ta="center">Invalid username/password</Text>}
        <Button fullWidth mt="xl" onClick={handleSubmit}>
          Sign in
        </Button>
      </Paper>
    </Container>
  );
}