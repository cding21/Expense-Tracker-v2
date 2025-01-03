'use client';

import { Button } from '@mantine/core';
import { useMutation } from '@tanstack/react-query';
import { logout } from '@/helper/auth';

export function SignOut() {
  const mutation = useMutation({
    mutationFn: () => logout(),
    onSuccess: () => {
      // Do nothing
    },
  });

  return (
    <Button
      variant="outline"
      color="red"
      fullWidth
      onClick={() => mutation.mutate()}
      loading={mutation.isPending}
    >
      Sign out
    </Button>
  );
}
