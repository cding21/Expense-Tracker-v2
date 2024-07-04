import { Container, Title, Text } from '@mantine/core';

export default function GetSupport() {
	return (
		<Container style={{ marginTop: '250px', marginBottom: '500px' }}>
			<Title ta="center">Support</Title>
			<Text ta="center">
				For support, please email&nbsp;
				<a href="mailto:budgetbuddy@gmail.com">budgetbuddy@gmail.com</a>
			</Text>
		</Container>
	);
}
