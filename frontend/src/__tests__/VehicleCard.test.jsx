import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import VehicleCard from '../features/vehicles/VehicleCard';

vi.mock('../hooks/useAuth', () => ({
  useAuth: () => ({ isAdmin: false, user: { username: 'test', role: 'USER' } }),
}));

const baseVehicle = {
  id: 1, make: 'Toyota', model: 'Camry', category: 'Sedan',
  price: 27400, quantity: 5,
};

function renderCard(props = {}) {
  return render(
    <MemoryRouter>
      <VehicleCard vehicle={baseVehicle} {...props} />
    </MemoryRouter>
  );
}

describe('VehicleCard', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('renders make and model correctly', () => {
    renderCard();
    expect(screen.getByText('Toyota Camry')).toBeInTheDocument();
  });

  it('shows formatted price as currency', () => {
    renderCard();
    expect(screen.getByText('$27,400')).toBeInTheDocument();
  });

  it('shows Out of Stock badge when quantity is 0', () => {
    renderCard({ vehicle: { ...baseVehicle, quantity: 0 } });
    expect(screen.getByText('Out of stock')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /out of stock/i })).toBeDisabled();
  });

  it('enables purchase button when quantity > 0', () => {
    renderCard();
    expect(screen.getByRole('button', { name: /purchase/i })).toBeEnabled();
  });

  it('shows admin buttons when admin prop is true', () => {
    renderCard({ admin: true });
    expect(screen.getByRole('button', { name: /restock/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /delete/i })).toBeInTheDocument();
  });

  it('hides admin buttons when admin prop is false', () => {
    renderCard();
    expect(screen.queryByRole('button', { name: /restock/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /delete/i })).not.toBeInTheDocument();
  });

  it('calls onPurchase when purchase button clicked', async () => {
    const onPurchase = vi.fn();
    renderCard({ onPurchase });
    await userEvent.click(screen.getByRole('button', { name: /purchase/i }));
    expect(onPurchase).toHaveBeenCalledWith(baseVehicle);
  });

  it('calls onDelete when delete button clicked', async () => {
    const onDelete = vi.fn();
    renderCard({ admin: true, onDelete });
    await userEvent.click(screen.getByRole('button', { name: /delete/i }));
    expect(onDelete).toHaveBeenCalledWith(baseVehicle);
  });
});
