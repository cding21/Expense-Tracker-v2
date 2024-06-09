import { render, screen } from '@/test-utils';
import { Welcome } from './Welcome';

describe('Welcome component', () => {
  it('has correct Next.js theming section link', () => {
    render(<Welcome />);
    expect(screen.getByText('Click here')).toHaveAttribute(
      'href',
      'https://github.com/cding21/Expense-Tracker-v2'
    );
  });
});
