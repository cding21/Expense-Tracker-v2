import {
  IconBulb,
  IconCheckbox,
  IconMessageCircle,
  IconPhoto,
  IconPlus,
  IconSearch,
  IconSettings,
  IconUser,
} from '@tabler/icons-react';
import {
  ActionIcon,
  Badge,
  Box,
  Group,
  Menu,
  rem,
  Text,
  Tooltip,
  UnstyledButton,
} from '@mantine/core';
import { forwardRef } from 'react';
import classes from './NavBar.module.css';
import { UserButton } from '../UserButton/UserButton';
import { SignOut } from '../SignOut/SignOut';

const links = [
  { icon: IconBulb, label: 'Dashboard', href: '/' },
  { icon: IconCheckbox, label: 'Transactions', notifications: 5, href: '/transactions' },
  { icon: IconUser, label: 'Contacts', href: '/contacts' },
];

const collections = [
  { emoji: '👍', label: 'Sales', href: '/sales' },
  { emoji: '🚚', label: 'Deliveries', href: '/deliveries' },
  { emoji: '💸', label: 'Discounts', href: '/discounts' },
  { emoji: '💰', label: 'Profits', href: '/profits' },
  { emoji: '✨', label: 'Reports', href: '/reoprts' },
  { emoji: '🛒', label: 'Orders', href: '/orders' },
  { emoji: '📅', label: 'Events', href: '/events' },
  { emoji: '🙈', label: 'Debts', href: '/debts' },
  { emoji: '💁‍♀️', label: 'Customers', href: '/customers' },
];

const CustomButton = forwardRef<HTMLDivElement, React.ComponentPropsWithoutRef<'div'>>(
  (props, ref) => (
    <div ref={ref} {...props}>
      <UserButton
        image="https://raw.githubusercontent.com/mantinedev/mantine/master/.demo/avatars/avatar-8.png"
        name="Harriet Schmidt"
        email="harrietschmidt@outlook.com"
      />
    </div>
  )
);

export function NavBar() {
  const mainLinks = links.map((link) => (
    <UnstyledButton
      key={link.label}
      className={classes.mainLink}
      onClick={() => window.location.assign(link.href)}
    >
      <div className={classes.mainLinkInner}>
        <link.icon size={20} className={classes.mainLinkIcon} stroke={1.5} />
        <span>{link.label}</span>
      </div>
      {link.notifications && (
        <Badge size="sm" variant="filled" className={classes.mainLinkBadge}>
          {link.notifications}
        </Badge>
      )}
    </UnstyledButton>
  ));

  const collectionLinks = collections.map((collection) => (
    <a
      href="#"
      onClick={() => window.location.assign(collection.label)}
      key={collection.label}
      className={classes.collectionLink}
    >
      <Box component="span" mr={9} fz={16}>
        {collection.emoji}
      </Box>{' '}
      {collection.label}
    </a>
  ));

  return (
    <div className={classes.navbar}>
      <div className={classes.section}>
        <Menu position="right">
          <Menu.Target>
            <CustomButton />
          </Menu.Target>

          <Menu.Dropdown>
            <Menu.Item
              leftSection={<IconMessageCircle style={{ width: rem(14), height: rem(14) }} />}
            >
              Messages
            </Menu.Item>
            <Menu.Item leftSection={<IconPhoto style={{ width: rem(14), height: rem(14) }} />}>
              Gallery
            </Menu.Item>
            <Menu.Item
              leftSection={<IconSearch style={{ width: rem(14), height: rem(14) }} />}
              rightSection={
                <Text size="xs" c="dimmed">
                  ⌘K
                </Text>
              }
            >
              Search
            </Menu.Item>

            <Menu.Divider />
            <Menu.Item disabled>
              <Text c="dimmed" size="sm">
                Settings
              </Text>
            </Menu.Item>
            <Menu.Item leftSection={<IconSettings style={{ width: rem(14), height: rem(14) }} />}>
              Account Settings
            </Menu.Item>
            <Menu.Divider />
            <Menu.Item>
              <SignOut />
            </Menu.Item>
          </Menu.Dropdown>
        </Menu>
      </div>

      <div className={classes.section}>
        <div className={classes.mainLinks}>{mainLinks}</div>
      </div>

      <div className={classes.section}>
        <Group className={classes.collectionsHeader} justify="space-between">
          <Text size="xs" fw={500} c="dimmed">
            Collections
          </Text>
          <Tooltip label="Create collection" withArrow position="right">
            <ActionIcon variant="default" size={18}>
              <IconPlus size={12} stroke={1.5} />
            </ActionIcon>
          </Tooltip>
        </Group>
        <div className={classes.collections}>{collectionLinks}</div>
      </div>
    </div>
  );
}
