import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import VehicleDetailPage from '../features/vehicles/VehicleDetailPage';

const mockVehicle = {
  id: 1, make: 'Toyota', model: 'Camry', category: 'Sedan',
  price: 27400, quantity: 5,
};

const mockGetVehicle = vi.fn();
const mockPurchaseVehicle = vi.fn();
const mockRestockVehicle = vi.fn();
const mockDeleteVehicle = vi.fn();

vi.mock('../api/vehicleApi', () => ({
  getVehicleById: (...args) => mockGetVehicle(...args),
  purchaseVehicle: (...args) => mockPurchaseVehicle(...args),
  restockVehicle: (...args) => mockRestockVehicle(...args),
  deleteVehicle: (...args) => mockDeleteVehicle(...args),
}));

vi.mock('../hooks/useAuth', () => ({
  useAuth: vi.fn(),
}));

vi.mock('sonner', () => ({
  toast: { success: vi.fn(), error: vi.fn() },
}));

import { useAuth } from '../hooks/useAuth';

function renderDetail(id = '1') {
  return render(
    <MemoryRouter initialEntries={[`/vehicles/${id}`]}>
      <Routes>
        <Route path="/vehicles/:id" element={<VehicleDetailPage />} />
        <Route path="/dashboard" element={<div>Dashboard Page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe('VehicleDetailPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    useAuth.mockReturnValue({ isAdmin: false, user: { username: 'test', role: 'USER' } });
    mockGetVehicle.mockResolvedValue({ data: mockVehicle });
  });

  it('shows loading state initially', () => {
    mockGetVehicle.mockReturnValue(new Promise(() => {}));
    renderDetail();
    expect(screen.getByText('Loading vehicle...')).toBeInTheDocument();
  });

  it('renders vehicle details after loading', async () => {
    renderDetail();
    expect(await screen.findByText('Toyota Camry')).toBeInTheDocument();
    expect(screen.getByText('$27,400')).toBeInTheDocument();
    expect(screen.getByText(/in stock/i)).toBeInTheDocument();
  });

  it('shows purchase button', async () => {
    renderDetail();
    expect(await screen.findByRole('button', { name: /purchase/i })).toBeEnabled();
  });

  it('disables purchase button when out of stock', async () => {
    mockGetVehicle.mockResolvedValue({ data: { ...mockVehicle, quantity: 0 } });
    renderDetail();
    expect(await screen.findByRole('button', { name: /purchase/i })).toBeDisabled();
  });

  it('shows admin buttons when user is admin', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    renderDetail();
    expect(await screen.findByRole('button', { name: /restock/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /delete/i })).toBeInTheDocument();
  });

  it('hides admin buttons for regular users', async () => {
    renderDetail();
    expect(await screen.findByRole('button', { name: /purchase/i })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /restock/i })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /delete/i })).not.toBeInTheDocument();
  });

  it('calls purchaseVehicle on purchase click', async () => {
    mockPurchaseVehicle.mockResolvedValue({ data: mockVehicle });
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /purchase/i }));
    expect(mockPurchaseVehicle).toHaveBeenCalledWith('1', 1);
  });

  it('calls restockVehicle on restock click', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    mockRestockVehicle.mockResolvedValue({ data: mockVehicle });
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /restock/i }));
    expect(mockRestockVehicle).toHaveBeenCalledWith('1', 1);
  });

  it('navigates back to dashboard on back click', async () => {
    renderDetail();
    await userEvent.click(await screen.findByText(/back to dashboard/i));
    expect(screen.getByText('Dashboard Page')).toBeInTheDocument();
  });

  it('shows error toast when vehicle not found', async () => {
    const { toast } = await import('sonner');
    mockGetVehicle.mockRejectedValue(new Error('Not found'));
    renderDetail();
    await vi.waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Vehicle not found');
    });
  });

  it('shows error toast on purchase failure', async () => {
    const { toast } = await import('sonner');
    mockPurchaseVehicle.mockRejectedValue({ response: { data: { message: 'Insufficient stock' } } });
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /purchase/i }));
    await vi.waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Insufficient stock');
    });
  });

  it('shows fallback error toast on purchase failure without response', async () => {
    const { toast } = await import('sonner');
    mockPurchaseVehicle.mockRejectedValue(new Error('Network error'));
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /purchase/i }));
    await vi.waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Purchase failed');
    });
  });

  it('shows error toast on restock failure', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    const { toast } = await import('sonner');
    mockRestockVehicle.mockRejectedValue({ response: { data: { message: 'Restock limit exceeded' } } });
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /restock/i }));
    await vi.waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Restock limit exceeded');
    });
  });

  it('shows fallback error toast on restock failure without response', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    const { toast } = await import('sonner');
    mockRestockVehicle.mockRejectedValue(new Error('Network error'));
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /restock/i }));
    await vi.waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Restock failed');
    });
  });

  it('does not delete when confirm is cancelled', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    const winConfirm = vi.spyOn(window, 'confirm').mockReturnValue(false);
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /delete/i }));
    expect(mockDeleteVehicle).not.toHaveBeenCalled();
    winConfirm.mockRestore();
  });

  it('handles delete successfully', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    const { toast } = await import('sonner');
    const winConfirm = vi.spyOn(window, 'confirm').mockReturnValue(true);
    mockDeleteVehicle.mockResolvedValue({});
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /delete/i }));
    await vi.waitFor(() => {
      expect(mockDeleteVehicle).toHaveBeenCalledWith('1');
      expect(toast.success).toHaveBeenCalledWith('Vehicle deleted');
    });
    expect(screen.getByText('Dashboard Page')).toBeInTheDocument();
    winConfirm.mockRestore();
  });

  it('shows error toast on delete failure', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    const { toast } = await import('sonner');
    const winConfirm = vi.spyOn(window, 'confirm').mockReturnValue(true);
    mockDeleteVehicle.mockRejectedValue({ response: { data: { message: 'Cannot delete' } } });
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /delete/i }));
    await vi.waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Cannot delete');
    });
    winConfirm.mockRestore();
  });

  it('shows fallback error toast on delete failure without response', async () => {
    useAuth.mockReturnValue({ isAdmin: true, user: { username: 'admin', role: 'ADMIN' } });
    const { toast } = await import('sonner');
    const winConfirm = vi.spyOn(window, 'confirm').mockReturnValue(true);
    mockDeleteVehicle.mockRejectedValue(new Error('Network error'));
    renderDetail();
    await userEvent.click(await screen.findByRole('button', { name: /delete/i }));
    await vi.waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Delete failed');
    });
    winConfirm.mockRestore();
  });

  it('updates quantity on input change', async () => {
    renderDetail();
    const qtyInput = await screen.findByLabelText(/quantity/i);
    await userEvent.tripleClick(qtyInput);
    await userEvent.keyboard('3');
    expect(qtyInput).toHaveValue(3);
  });

  it('falls back to 1 on invalid quantity input', async () => {
    renderDetail();
    const qtyInput = await screen.findByLabelText(/quantity/i);
    await userEvent.clear(qtyInput);
    expect(qtyInput).toHaveValue(1);
  });
});
