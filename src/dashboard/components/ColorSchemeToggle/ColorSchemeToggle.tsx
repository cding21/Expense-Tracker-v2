'use client';

import { Button, Group, useMantineColorScheme } from '@mantine/core';
import { FaMoon, FaSun } from 'react-icons/fa';
import { BsNintendoSwitch } from 'react-icons/bs';

export function ColorSchemeToggle() {
  const { setColorScheme } = useMantineColorScheme();

  return (
    <Group justify="center" mt="xl">
      <Button onClick={() => setColorScheme('light')}>
        <FaSun />
      </Button>
      <Button onClick={() => setColorScheme('dark')}>
        <FaMoon />
      </Button>
      <Button onClick={() => setColorScheme('auto')}>
        <BsNintendoSwitch />
      </Button>
    </Group>
  );
}
