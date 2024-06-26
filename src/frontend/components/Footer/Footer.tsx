'use client';

import { Container, Group, Anchor, Loader, Text } from '@mantine/core';
import { useState, useEffect } from 'react';
import classes from './Footer.module.css';

const links = [
  { link: 'contact', label: 'Contact' },
  { link: 'privacy', label: 'Privacy' },
  { link: 'blog', label: 'Blog' },
  { link: 'careers', label: 'Careers' },
];

async function probeBackend() {
  try {
    // Provide a default URL if BACKEND_URL is undefined
    const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0';
    const res = await fetch(`${backendUrl}/health`);
    return res;
  } catch (e) {
    return { ok: false };
  }
}

export function Footer() {
  const [serverStatus, setServerStatus] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const result = async () => {
      const res = await probeBackend();
      setLoading(false);
      setServerStatus(res.ok);
    };
    setInterval(result, 10000);
  }, []);

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
          <span key="status" style={{ color: serverStatus ? 'green' : 'red' }}>
            {loading ? <Loader size={15} /> : '⬤'}
          </span>
        </Text>
        <Group className={classes.links}>{items}</Group>
      </Container>
    </div>
  );
}
