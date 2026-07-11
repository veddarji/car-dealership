import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import VehicleForm from '../features/vehicles/VehicleForm';

const sampleVehicle = {
  id: 1, make: 'Toyota', model: 'Camry', category: 'Sedan',
  price: 27400, quantity: 5,
};

describe('VehicleForm', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  const renderForm = (props = {}) =>
    render(
      <VehicleForm
        isOpen={true}
        onClose={vi.fn()}
        onSubmit={vi.fn()}
        title="Test Form"
        {...props}
      />
    );

  it('renders form fields for vehicle creation', () => {
    renderForm();
    expect(screen.getByLabelText(/make/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/model/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/category/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/price/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/quantity/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /create/i })).toBeInTheDocument();
  });

  it('pre-fills fields in edit mode', () => {
    renderForm({ initialData: sampleVehicle });
    expect(screen.getByLabelText(/make/i)).toHaveValue('Toyota');
    expect(screen.getByLabelText(/model/i)).toHaveValue('Camry');
    expect(screen.getByLabelText(/price/i)).toHaveValue(27400);
    expect(screen.getByLabelText(/quantity/i)).toHaveValue(5);
    expect(screen.getByRole('button', { name: /update/i })).toBeInTheDocument();
  });

  it('validates required fields', async () => {
    renderForm({ initialData: { ...sampleVehicle, make: '' } });
    await userEvent.click(screen.getByRole('button', { name: /update/i }));
    expect(screen.getByText('Required')).toBeInTheDocument();
  });

  it('validates price must be > 0', async () => {
    renderForm({ initialData: { ...sampleVehicle, price: 0 } });
    await userEvent.click(screen.getByRole('button', { name: /update/i }));
    expect(screen.getByText('Must be > 0')).toBeInTheDocument();
  });

  it('calls onSubmit with correct data', async () => {
    const onSubmit = vi.fn();
    const onClose = vi.fn();

    render(
      <VehicleForm
        isOpen={true}
        onClose={onClose}
        onSubmit={onSubmit}
        title="Add Vehicle"
      />
    );

    await userEvent.type(screen.getByLabelText(/make/i), 'Honda');
    await userEvent.type(screen.getByLabelText(/model/i), 'Accord');

    const categorySelect = screen.getByLabelText(/category/i);
    await userEvent.selectOptions(categorySelect, 'Sedan');

    await userEvent.type(screen.getByLabelText(/price/i), '30000');
    await userEvent.type(screen.getByLabelText(/quantity/i), '10');
    await userEvent.click(screen.getByRole('button', { name: /create/i }));

    expect(onSubmit).toHaveBeenCalledWith({
      make: 'Honda', model: 'Accord', category: 'Sedan',
      price: 30000, quantity: 10,
    });
  });

  it('calls onClose when cancel is clicked', async () => {
    const onClose = vi.fn();
    renderForm({ onClose });
    await userEvent.click(screen.getByRole('button', { name: /cancel/i }));
    expect(onClose).toHaveBeenCalled();
  });
});
