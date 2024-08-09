'use client';

import { logout } from '@/auth';
import { Button } from '@mantine/core';
import { QueryClient, useMutation, QueryClientProvider } from '@tanstack/react-query';

// Create a client
const queryClient = new QueryClient();

export function SignOut() {
	return (
		<QueryClientProvider client={queryClient}>
			<SignOutComponent />
		</QueryClientProvider>
	);
}

export function SignOutComponent() {
	const mutation = useMutation({
		mutationFn: () => logout(),
		onSuccess: () => {
			// Reload the page
			window.location.href = "/sign-in";
		},
		onError: (error) => {
			console.log(error);
		},
	});

  return (
		<Button variant="outline" color="red" fullWidth mt="md" onClick={() => mutation.mutate()} loading={mutation.isPending}>
			Sign out
		</Button>
  );
}