'use client';

import { Menu, Group, Center, Burger, Container, Button, Anchor, Accordion, Stack, Drawer } from '@mantine/core';
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
  const dashboardURL = process.env.NEXT_PUBLIC_DASHBOARD_URL ?? 'http://localhost:3001';

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

  mobileItems.push(
    <Button 
      component="a" 
      href={dashboardURL} 
      mt="md"
      target="_blank" 
      style={{ display: 'fixed', justifyContent: 'center' }}
    >
      Dashboard
    </Button>
  );

  const items = links.map((link) => {
    const menuItems = link.links?.map((item) => (
      <Menu.Item key={item.link} component="a" href={item.link}>
        {item.label}
      </Menu.Item>
    ));

    if (menuItems) {
      return (
        <Menu key={link.label} trigger="hover" transitionProps={{ exitDuration: 0 }} withinPortal>
          <Menu.Target>
            <a href={link.link} className={classes.link}>
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
      <a key={link.label} href={link.link} className={classes.link}>
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
          <Anchor component="a" href="/" underline="never">
            Budget Buddy
          </Anchor>
          <Group gap={5} visibleFrom="sm">
            {items}
            <Button component="a" href={dashboardURL} target="_blank">
              Dashboard
            </Button>
          </Group>
          <Burger opened={opened} onClick={toggle} size="sm" hiddenFrom="sm" />
        </div>
      </Container>
    </header>
  );
}
