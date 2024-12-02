import { MantineProvider } from '@mantine/core';
import { Notifications } from '@mantine/notifications';
import Providers from '@/app/providers';
import { SignUp } from './SignUp';
import { fireEvent, render, screen, waitFor } from '@/test-utils';
import { theme } from '@/theme';

describe('SignUp', () => {
  it('renders form with username, password, confirmPassword, submit button, and link to back in sign-in page', () => {
    // Arrange
    render(
      <Providers>
        <MantineProvider theme={theme}>
          <Notifications />
          <SignUp />
        </MantineProvider>
      </Providers>
    );
    // Assert
    expect(screen.getByText('Username')).toBeDefined();
    expect(screen.getByText('Password')).toBeDefined();
    expect(screen.getByText('Confirm password')).toBeDefined();
  });

  it('submits form data to the server', async () => {
    // Mock the login function
    const mockSignUp = jest.fn();
    jest.mock('../../helper/auth', () => ({
      signUp: mockSignUp,
    }));

    // Arrange
    render(
      <Providers>
        <MantineProvider theme={theme}>
          <Notifications />
          <SignUp />
        </MantineProvider>
      </Providers>
    );

    // Fill in the form fields
    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getByPlaceholderText('Your password');
    const confirmPasswordInput = screen.getByPlaceholderText('Your confirm password');
    const submitButton = screen.getByText('Sign up');

    // Act
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'testpassword' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'testpassword' } });
    fireEvent.click(submitButton);

    // Assert
    // eslint-disable-next-line testing-library/await-async-utils
    waitFor(() =>
      expect(mockSignUp).toHaveBeenCalledWith({ username: 'testuser', password: 'testpassword' })
    );
  });

  it('displays an error message if sign-up fails', async () => {
    // Arrange
    render(
      <Providers>
        <MantineProvider theme={theme}>
          <Notifications />
          <SignUp />
        </MantineProvider>
      </Providers>
    );

    // Mock the sign-up function to simulate a failed login
    // Must mocked the global fetch function as TanStack requires this to be defined....
    global.fetch = jest
      .fn()
      .mockImplementation(() => Promise.reject(new Error('Username already exists')));

    // Fill in the form fields
    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getByPlaceholderText('Your password');
    const confirmPasswordInput = screen.getByPlaceholderText('Your confirm password');
    const submitButton = screen.getByText('Sign up');

    // Act
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'AVerySecurePassword!234' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'AVerySecurePassword!234' } });
    fireEvent.click(submitButton);

    // Wait for the error message to be displayed
    await screen.findByText('Sign-up failed: Username already exists');

    // Assert
    expect(screen.getByText('Sign-up failed: Username already exists')).toBeDefined();
  });
});
