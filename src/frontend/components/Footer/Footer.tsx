'use client';

import { Container, Group, Anchor, Loader, Text } from '@mantine/core';
import { useQuery } from '@tanstack/react-query';
import classes from './Footer.module.css';

const links = [
  { link: 'contact', label: 'Contact' },
  { link: 'privacy', label: 'Privacy' },
  { link: 'blog', label: 'Blog' },
  { link: 'careers', label: 'Careers' },
];

async function probeBackend() {
  // Provide a default URL if BACKEND_URL is undefined
  const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
  const res = await fetch(`${backendUrl}/health`);
  if (res.status === 200) {
    return { ok: true };
  }
  throw new Error('Server may be down');
}

export function Footer() {
  const { isSuccess, isLoading } = useQuery({
    queryKey: ['probeBackend'],
    queryFn: () => probeBackend(),
    refetchInterval: 5000,
  });

  const items = links.map((link) => (
    <Anchor<'a'>
      c="dimmed"
      key={link.label}
      href={link.link}
      onClick={() => {
        window.location.href = link.link;
      }}
      size="sm"
    >
      {link.label}
    </Anchor>
  ));

  return (
    <div className={classes.footer}>
      <Container className={classes.inner}>
        <Text c="dimmed" size="sm">
          Current server status: &nbsp;
          <span key="status" style={{ color: isSuccess ? 'green' : 'red' }}>
            {isLoading ? <Loader size={15} /> : '⬤'}
          </span>
        </Text>
        <Group className={classes.links}>{items}</Group>
      </Container>
    </div>
  );
}
