'use server';

import { cookies } from 'next/headers';
import { UserLogin } from '../models/user.model';
import { redirect } from 'next/navigation';

export async function login(loginData: UserLogin) {
  // Make an API request to authenticate the user
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(loginData),
  });
  // Check if the login was successful
  if (response.status === 200) {
    // Set session JWT token
    const resp = await response.json();
    cookies().set('token', resp.token, { sameSite: 'strict' });
    redirect('/');
  } else {
    const resp = await response.text();
    throw new Error(resp);
  }
}

export async function signUp(signUpData: UserLogin) {
  // Make an API request to authenticate the user
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const response = await fetch(`${backendUrl}/signup`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(signUpData),
  });

  // Check if the login was successful
  if (response.status === 200) {
    const resp = await response.text();
    redirect('/sign-in');
  } else {
    const resp = await response.text();
    throw new Error(resp);
  }
}

export async function logout() {
  // Remove the session JWT token
  cookies().delete('token');
  redirect('/sign-in');
}
