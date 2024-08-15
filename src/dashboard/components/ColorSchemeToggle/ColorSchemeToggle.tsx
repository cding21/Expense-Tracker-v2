'use client';

import { Button, Group, useMantineColorScheme } from '@mantine/core';
import { FaMoon, FaSun } from 'react-icons/fa';
import { BsNintendoSwitch } from 'react-icons/bs';

export function ColorSchemeToggle() {
  const { colorScheme, toggleColorScheme } = useMantineColorScheme();
  const theme = {
    light: {
      icon: <FaSun />,
      variant: 'outline',
    },
    dark: {
      icon: <FaMoon />,
      variant: 'filled',
    },
    auto: {
      icon: <BsNintendoSwitch />,
      variant: 'light',
    },
  };

  return (
    <Group justify="center" mt="xl">
      <Button variant={theme[colorScheme].variant} onClick={() => toggleColorScheme()} color="gray">
        {theme[colorScheme].icon}
      </Button>
    </Group>
  );
}
