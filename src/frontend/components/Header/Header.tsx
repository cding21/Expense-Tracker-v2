'use client';

import {
  Menu,
  Group,
  Center,
  Burger,
  Container,
  Text,
  Button,
  Drawer,
  Accordion,
  Stack,
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import {
  IconChevronDown,
  IconEdit,
  IconEraser,
  IconFileInfo,
  IconHelpHexagon,
  IconPlus,
  IconZoom,
} from '@tabler/icons-react';
import classes from './Header.module.css';

const links = [
  {
    link: '#1',
    label: 'Transactions',
    links: [
      { link: '/Create', label: 'Create', icon: <IconPlus size="1.1rem" /> },
      { link: '/Lookup', label: 'Lookup', icon: <IconZoom size="1.1rem" /> },
      { link: '/Update', label: 'Update', icon: <IconEdit size="1.1rem" /> },
      { link: '/Delete', label: 'Delete', icon: <IconEraser size="1.1rem" /> },
    ],
  },
  {
    link: '#2',
    label: 'Support',
    links: [
      { link: '/faq', label: 'FAQ', icon: <IconHelpHexagon size="1.1rem" /> },
      { link: '/documentations', label: 'Docs', icon: <IconFileInfo size="1.1rem" /> },
    ],
  },
];

export function Header() {
  const [opened, { toggle }] = useDisclosure(false);

  const mobileItems = links.map((item) => (
    <Accordion.Item key={item.link} value={item.link}>
      <Accordion.Control>{item.label}</Accordion.Control>
      <Accordion.Panel>
        {item.links.map((subItem) => {
          const { icon } = subItem;
          return (
            <Stack py="xs">
              <Button fullWidth rightSection={icon} justify="space-between" variant="default">
                {subItem.label}
              </Button>
            </Stack>
          );
        })}
      </Accordion.Panel>
    </Accordion.Item>
  ));

  const items = links.map((link) => {
    const menuItems = link.links?.map((item) => (
      <Menu.Item key={item.link}>{item.label}</Menu.Item>
    ));

    if (menuItems) {
      return (
        <Menu key={link.label} trigger="hover" transitionProps={{ exitDuration: 0 }} withinPortal>
          <Menu.Target>
            <a
              href={link.link}
              className={classes.link}
              onClick={(event) => event.preventDefault()}
            >
              <Center>
                <span className={classes.linkLabel}>{link.label}</span>
                <IconChevronDown size="0.9rem" stroke={1.5} />
              </Center>
            </a>
          </Menu.Target>
          <Menu.Dropdown>{menuItems}</Menu.Dropdown>
        </Menu>
      );
    }

    return (
      <a
        key={link.label}
        href={link.link}
        className={classes.link}
        onClick={(event) => event.preventDefault()}
      >
        {link.label}
      </a>
    );
  });

  return (
    <header className={classes.header}>
      <Drawer opened={opened} onClose={toggle}>
        <Accordion variant="separated" radius="md">
          {mobileItems}
        </Accordion>
      </Drawer>

      <Container size="md">
        <div className={classes.inner}>
          <Text>Expense Tracker</Text>
          <Group gap={5} visibleFrom="sm">
            {items}
            <Button>Sign in</Button>
          </Group>
          <Burger opened={opened} onClick={toggle} size="sm" hiddenFrom="sm" />
        </div>
      </Container>
    </header>
  );
}
