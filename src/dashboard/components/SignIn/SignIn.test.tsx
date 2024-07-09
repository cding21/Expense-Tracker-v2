import { SignIn } from './SignIn';
import { fireEvent, render, screen, waitFor } from '@/test-utils';

describe('SignIn', () => {
  it('renders form with username, password, submit button, and link to sign up', () => {
    // Arrange
    render(<SignIn />);
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
    render(<SignIn />);

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
    render(<SignIn />);

    // Mock the login function to simulate a failed login
    const mockLogin = jest.fn().mockRejectedValue(new Error('Login failed'));
    jest.mock('../../auth', () => ({
      login: mockLogin,
    }));

    // Fill in the form fields
    const usernameInput = screen.getByPlaceholderText('Your username');
    const passwordInput = screen.getByPlaceholderText('Your password');
    const submitButton = screen.getByText('Sign in');

    // Act
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'testpassword' } });
    fireEvent.click(submitButton);

    // Wait for the error message to be displayed
    await screen.findByText('Invalid username/password');

    // Assert
    expect(screen.getByText('Invalid username/password')).toBeDefined();
  });
});
