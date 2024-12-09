import { MantineProvider } from '@mantine/core';
import { Notifications } from '@mantine/notifications';
import Providers from '@/app/providers';
import { SignUp } from './SignUp';
import { fireEvent, render, screen, waitFor } from '@/test-utils';
import { theme } from '@/theme';
import { checkUsername, signUp } from '@/helper/auth';

// Mock the login function
jest.mock('../../helper/auth', () => ({
  signUp: jest.fn(),
  checkUsername: jest.fn(),
}));

const mockSignUp = signUp as jest.Mock;
const mockCheckUsername = checkUsername as jest.Mock;

describe('SignUp', () => {
  beforeEach(() => {
    mockCheckUsername.mockReset();
    mockSignUp.mockReset();
  });
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
    // Mock the signUp function
    mockCheckUsername.mockReturnValueOnce(Promise.resolve(true));
    mockSignUp.mockReturnValueOnce(Promise.resolve());

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
    waitFor(() => {
      expect(mockCheckUsername).toHaveBeenCalled();
    });
    // eslint-disable-next-line testing-library/await-async-utils
    waitFor(() => {
      expect(mockSignUp).toHaveBeenCalledWith({ username: 'testuser', password: 'testpassword' });
    });
  });

  it('displays an error message if username check fails', async () => {
    const msg = 'Username is not available';

    // Mock the signUp function
    mockCheckUsername.mockReturnValueOnce(Promise.reject(new Error(msg)));

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
    const submitButton = screen.getByText('Sign up');

    // Act
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.click(submitButton);

    // Wait for the error message to be displayed
    await screen.findByText(msg);

    // Assert
    expect(screen.getByText(msg)).toBeDefined();
  });

  it('displays an error message if sign-up fails', async () => {
    const msg = 'An error occurred';
    // Mock the signUp function
    mockCheckUsername.mockReturnValueOnce(Promise.resolve(true));
    mockSignUp.mockRejectedValueOnce(new Error(msg));

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
    fireEvent.change(passwordInput, { target: { value: 'AVerySecurePassword!234' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'AVerySecurePassword!234' } });
    fireEvent.click(submitButton);

    // Wait for the error message to be displayed
    await screen.findByText(`Sign-up failed: ${msg}`);

    // Assert
    expect(screen.getByText(`Sign-up failed: ${msg}`)).toBeDefined();
  });
});
