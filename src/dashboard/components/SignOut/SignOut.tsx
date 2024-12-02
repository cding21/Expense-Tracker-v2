'use client';

import { Button } from '@mantine/core';
import { useMutation } from '@tanstack/react-query';
import { logout } from '@/helper/auth';

export function SignOut() {
  const mutation = useMutation({
    mutationFn: () => logout(),
    onSuccess: () => {
      // Redirect to the sign-in page
      window.location.href = '/sign-in';
    },
  });

  return (
    <Button
      variant="outline"
      color="red"
      fullWidth
      mt="md"
      onClick={() => mutation.mutate()}
      loading={mutation.isPending}
    >
      Sign out
    </Button>
  );
}
