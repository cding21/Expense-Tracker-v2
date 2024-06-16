'use client';

import { Container, Group, Anchor, Loader, Text } from '@mantine/core';
import { MantineLogo } from '@mantinex/mantine-logo';
import classes from './Footer.module.css';
import { useState, useEffect } from 'react';

const links = [
  { link: 'contact', label: 'Contact' },
  { link: 'privacy', label: 'Privacy' },
  { link: 'blog', label: 'Blog' },
  { link: 'careers', label: 'Careers' },
];

async function probeBackend() {
	// Provide a default URL if BACKEND_URL is undefined
	const backendUrl = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080/api/v0'; 
	try {
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
        {/* <MantineLogo size={28} /> */}
		<Anchor<'a'>
			c="dimmed"
			size="sm"
		>
			Current server status: 
			<span key='status' style={{ color: serverStatus ? 'green' : 'red' }}>
				{loading ? <Loader size={15}/> : ' ⬤ '}
			</span>
		</Anchor>
        <Group className={classes.links}>{items}</Group>
        
      </Container>
    </div>
  );
}