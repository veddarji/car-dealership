import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import VehicleGrid from '../features/vehicles/VehicleGrid';

vi.mock('../hooks/useAuth', () => ({
  useAuth: () => ({ isAdmin: false, user: { username: 'test', role: 'USER' } }),
}));

const sampleVehicles = [
  { id: 1, make: 'Toyota', model: 'Camry', category: 'Sedan', price: 27400, quantity: 5 },
  { id: 2, make: 'Honda', model: 'Civic', category: 'Sedan', price: 25000, quantity: 3 },
];

describe('VehicleGrid', () => {
  const renderGrid = (props = {}) =>
    render(
      <MemoryRouter>
        <VehicleGrid vehicles={[]} loading={false} error={null} {...props} />
      </MemoryRouter>
    );

  it('shows loading skeleton when loading', () => {
    renderGrid({ loading: true });
    const skeletons = document.querySelectorAll('.skeleton-card');
    expect(skeletons.length).toBeGreaterThanOrEqual(1);
  });

  it('shows empty message when vehicles array is empty', () => {
    renderGrid({ vehicles: [] });
    expect(screen.getByText('No vehicles found')).toBeInTheDocument();
  });

  it('renders vehicle cards when data provided', () => {
    renderGrid({ vehicles: sampleVehicles });
    expect(screen.getByText('Toyota Camry')).toBeInTheDocument();
    expect(screen.getByText('Honda Civic')).toBeInTheDocument();
  });

  it('shows error message on error', () => {
    renderGrid({ error: 'Something went wrong' });
    expect(screen.getByRole('heading', { name: 'Something went wrong' })).toBeInTheDocument();
  });

  it('renders retry button when error and onRetry provided', () => {
    const onRetry = vi.fn();
    renderGrid({ error: 'Failed', onRetry });
    expect(screen.getByRole('button', { name: /retry/i })).toBeInTheDocument();
  });
});
