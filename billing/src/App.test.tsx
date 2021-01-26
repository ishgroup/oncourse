import React from 'react';
import { render, screen } from '@testing-library/react';
import Billing from './Billing';

test('renders learn react link', () => {
  render(<Billing />);
  const linkElement = screen.getByText(/learn react/i);
  expect(linkElement).toBeInTheDocument();
});
