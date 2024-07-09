'use client';

import { Menu, Group, Center, Burger, Container, Text, Button, Anchor } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconChevronDown, IconFileInfo, IconHelpHexagon } from '@tabler/icons-react';
import classes from './Header.module.css';

const links = [
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
  const dashboardURL = process.env.NEXT_PUBLIC_DASHBOARD_URL??'http://localhost:3001';

  const items = links.map((link) => {
    const menuItems = link.links?.map((item) => (
      <Menu.Item
        key={item.link}
        component="a"
        href={item.link}
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
      >
        {link.label}
      </a>
    );
  });

  return (
    <header className={classes.header}>
      <Container size="md">
        <div className={classes.inner}>
          <Anchor 
            component="a"
            href="/"
            underline="never"
          >
            Budget Buddy
          </Anchor>
          <Group gap={5} visibleFrom="sm">
            {items}
            <Button 
              component="a"
              href={dashboardURL}
              target="_blank"
            >
              Dashboard
            </Button>
          </Group>
          <Burger opened={opened} onClick={toggle} size="sm" hiddenFrom="sm" />
        </div>
      </Container>
    </header>
  );
}
