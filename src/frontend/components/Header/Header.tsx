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
import { IconChevronDown, IconFileInfo, IconHelpHexagon } from '@tabler/icons-react';
import classes from './Header.module.css';

const links = [
  // TODO: Uncomment the following lines to add the Transactions link to the header
  // {
  //   link: '/transactions',
  //   label: 'Transactions',
  // },
  {
    link: '/support',
    label: 'Support',
    links: [
      { link: '/faq', label: 'FAQ', icon: <IconHelpHexagon size="1.1rem" /> },
      { link: '/documentation', label: 'Docs', icon: <IconFileInfo size="1.1rem" /> },
    ],
  },
];

export function Header() {
  const [opened, { toggle }] = useDisclosure(false);

  const items = links.map((link) => {
    const menuItems = link.links?.map((item) => (
      <Menu.Item
        key={item.link}
        onClick={() => {
          window.location.href = item.link;
        }}
      >
        {item.label}
      </Menu.Item>
    ));

    if (menuItems) {
      return (
        <Menu key={link.label} trigger="hover" transitionProps={{ exitDuration: 0 }} withinPortal>
          <Menu.Target>
            <a
              href={link.link}
              className={classes.link}
              onClick={() => {
                window.location.href = link.link;
              }}
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
        onClick={() => {
          window.location.href = link.link;
        }}
      >
        {link.label}
      </a>
    );
  });

  return (
    <header className={classes.header}>
      <Container size="md">
        <div className={classes.inner}>
          <Text
            onClick={() => {
              window.location.href = '/';
            }}
          >
            Budget Buddy
          </Text>
          <Group gap={5} visibleFrom="sm">
            {items}
            <Button
              onClick={() => {
                window.location.href = '/sign-in';
              }}
            >
              Sign in
            </Button>
          </Group>
          <Burger opened={opened} onClick={toggle} size="sm" hiddenFrom="sm" />
        </div>
      </Container>
    </header>
  );
}
