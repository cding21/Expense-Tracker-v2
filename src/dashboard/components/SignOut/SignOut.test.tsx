import { cookies } from 'next/headers';
import Providers from '@/app/providers';
import { SignOut } from './SignOut';
import { fireEvent, render, screen, waitFor } from '@/test-utils';

// Mock
jest.mock('next/headers', () => ({
  ...jest.requireActual('next/headers'),
  cookies: jest.fn(() => ({
    delete: jest.fn(),
  })),
}));

// Mock window.location methods
const originalLocation = window.location;
delete (window as any).location;
window.location = {
  ...originalLocation,
  assign: jest.fn(),
  replace: jest.fn(),
  reload: jest.fn(),
};

describe('SignOut', () => {
  it('clears jwt token upon click', () => {
    // Arrange
    render(
      <Providers>
        <SignOut />
      </Providers>
    );

    const signOutButton = screen.getByText('Sign out');

    // Act
    fireEvent.click(signOutButton);

    // Assert
    // eslint-disable-next-line testing-library/await-async-utils
    waitFor(() => expect(cookies().delete).toHaveBeenCalledWith('token'));
  });
});
