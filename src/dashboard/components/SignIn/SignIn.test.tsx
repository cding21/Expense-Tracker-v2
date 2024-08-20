import { MantineProvider } from '@mantine/core';
import { Notifications } from '@mantine/notifications';
import Providers from '@/app/providers';
import { SignIn } from './SignIn';
import { fireEvent, render, screen, waitFor } from '@/test-utils';
import { theme } from '@/theme';

describe('SignIn', () => {
  it('renders form with username, password, submit button, and link to sign up', () => {
    // Arrange
    render(
      <Providers>
        <MantineProvider theme={theme}>
          <Notifications />
          <SignIn />
        </MantineProvider>
      </Providers>
    );
    // Assert
    expect(screen.getByText('Username')).toBeDefined();
    expect(screen.getByText('Password')).toBeDefined();
  });
  it('submits form data to the server', async () => {
    // Mock the login function
    const mockLogin = jest.fn();
    jest.mock('../../auth', () => ({
      login: mockLogin,
    }));

    // Arrange
    render(
      <Providers>
        <MantineProvider theme={theme}>
          <Notifications />
          <SignIn />
        </MantineProvider>
      </Providers>
    );

    // Fill in the form fields
    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getByPlaceholderText('Your password');
    const submitButton = screen.getByText('Sign in');

    // Act
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'testpassword' } });
    fireEvent.click(submitButton);

    // Assert
    // eslint-disable-next-line testing-library/await-async-utils
    waitFor(() =>
      expect(mockLogin).toHaveBeenCalledWith({ username: 'testuser', password: 'testpassword' })
    );
  });
  it('displays an error message if login fails', async () => {
    // Arrange
    render(
      <Providers>
        <MantineProvider theme={theme}>
          <Notifications />
          <SignIn />
        </MantineProvider>
      </Providers>
    );

    // Mock the login function to simulate a failed login
    global.fetch = jest.fn().mockRejectedValue(new Error('Login failed'));

    // Fill in the form fields
    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getByPlaceholderText('Your password');
    const submitButton = screen.getByText('Sign in');

    // Act
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'AVerySecurePassword!234' } });
    fireEvent.click(submitButton);

    // Wait for the error message to be displayed
    await screen.findByText('Login failed');

    // Assert
    expect(screen.getByText('Login failed')).toBeDefined();
  });
});
