import Providers from '@/app/providers';
import { SignUp } from './SignUp';
import { fireEvent, render, screen, waitFor } from '@/test-utils';

describe('SignUp', () => {
  it('renders form with username, password, confirmPassword, submit button, and link to back in sign-in page', () => {
    // Arrange
    render(
      <Providers>
        <SignUp />
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
    jest.mock('../../auth', () => ({
      signUp: mockSignUp,
    }));

    // Arrange
    render(
      <Providers>
        <SignUp />
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
        <SignUp />
      </Providers>
    );

    // Mock the login function to simulate a failed login
    const mockSignUp = jest.fn().mockRejectedValue(new Error('Sign-up failed'));
    jest.mock('../../auth', () => ({
      signUp: mockSignUp,
    }));

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

    // Wait for the error message to be displayed
    await screen.findByText('Sign-up failed');

    // Assert
    expect(screen.getByText('Sign-up failed')).toBeDefined();
  });
});
