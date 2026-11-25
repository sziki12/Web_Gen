import React from 'react';
import { render, screen } from '@testing-library/react';
import LiveView from './pages/LiveView';

test('renders learn react link', () => {
  render(<LiveView />);
  const linkElement = screen.getByText(/learn react/i);
  expect(linkElement).toBeInTheDocument();
});
