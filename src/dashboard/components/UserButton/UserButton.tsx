import { IconChevronRight } from '@tabler/icons-react';
import { Avatar, Group, Text, UnstyledButton } from '@mantine/core';
import classes from './UserButton.module.css';

interface UserButtonProps extends React.ComponentPropsWithoutRef<'button'> {
  image: string;
  name: string;
  email: string;
  icon?: React.ReactNode;
}

export const UserButton: React.FC<UserButtonProps> = ({ image, name, email }, ref) => (
  <UnstyledButton
    ref={ref}
    style={{
      padding: 'var(--mantine-spacing-md)',
      color: 'var(--mantine-color-text)',
      borderRadius: 'var(--mantine-radius-sm)',
    }}
    className={classes.user}
  >
    <Group>
      <Avatar src={image} radius="xl" />

      <div style={{ flex: 1 }}>
        <Text size="sm" fw={500}>
          {name}
        </Text>

        <Text c="dimmed" size="xs">
          {email}
        </Text>
      </div>

      <IconChevronRight size={14} stroke={1.5} />
    </Group>
  </UnstyledButton>
);
