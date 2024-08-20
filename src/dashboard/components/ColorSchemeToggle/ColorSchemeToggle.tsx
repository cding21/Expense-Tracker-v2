'use client';

import { Button, Group, useMantineColorScheme } from '@mantine/core';
import { FaMoon, FaSun } from 'react-icons/fa';
import { BsNintendoSwitch } from 'react-icons/bs';

enum ThemeColorKey {
  light = 'light',
  dark = 'dark',
  auto = 'auto',
}

interface ThemeDescriptor {
  icon: React.ReactNode;
  variant: string;
}

interface Theme {
  [ThemeColorKey.light]: ThemeDescriptor;
  [ThemeColorKey.dark]: ThemeDescriptor;
  [ThemeColorKey.auto]: ThemeDescriptor;
}

export function ColorSchemeToggle() {
  const { colorScheme, toggleColorScheme } = useMantineColorScheme();
  const theme: Theme = {
    light: {
      icon: <FaSun />,
      variant: 'outline',
    },
    dark: {
      icon: <FaMoon />,
      variant: 'outline',
    },
    auto: {
      icon: <BsNintendoSwitch />,
      variant: 'outline',
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
