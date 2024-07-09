"use server"
import { cookies } from "next/headers";


export async function login(formData: any) {
  // Make an API request to authenticate the user
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  try {
    const response = await fetch(`${backendUrl}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formData),
    });
    // Check if the login was successful
    if (response.status === 200) {
      // Set session JWT token 
      const resp = await response.json();
      cookies().set('token', resp.token, {"sameSite": "strict"});
    } else {
      throw new Error('Login failed');
    }
  } catch(e) {
    if(e instanceof Error) {
			// Display an error message to the user
			throw new Error('Login failed, reason: ' + e.message);
    }
  }
}
