import { describe, it, expect, vi, beforeEach } from 'vitest';

const mockAxiosInstance = {
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  delete: vi.fn(),
  interceptors: {
    request: { use: vi.fn() },
    response: { use: vi.fn() },
  },
};

vi.mock('../api/axiosInstance', () => ({
  default: mockAxiosInstance,
}));

describe('authApi', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('login calls POST /api/auth/login', async () => {
    mockAxiosInstance.post.mockResolvedValue({ data: { token: 'abc' } });
    const { login } = await import('../api/authApi');
    const result = await login({ username: 'alice', password: 'pass' });
    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/auth/login', { username: 'alice', password: 'pass' });
    expect(result.data.token).toBe('abc');
  });

  it('register calls POST /api/auth/register', async () => {
    mockAxiosInstance.post.mockResolvedValue({ data: { token: 'xyz' } });
    const { register } = await import('../api/authApi');
    await register({ username: 'bob', email: 'b@t.com', password: 'pass1234' });
    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/auth/register', {
      username: 'bob', email: 'b@t.com', password: 'pass1234',
    });
  });
});

describe('vehicleApi', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('getAllVehicles calls GET /api/vehicles', async () => {
    mockAxiosInstance.get.mockResolvedValue({ data: { content: [] } });
    const { getAllVehicles } = await import('../api/vehicleApi');
    await getAllVehicles({ page: 0, size: 10 });
    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/vehicles', { params: { page: 0, size: 10 } });
  });

  it('createVehicle calls POST /api/vehicles', async () => {
    mockAxiosInstance.post.mockResolvedValue({ data: { id: 1 } });
    const { createVehicle } = await import('../api/vehicleApi');
    await createVehicle({ make: 'Honda', price: 25000 });
    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/vehicles', { make: 'Honda', price: 25000 });
  });

  it('deleteVehicle calls DELETE /api/vehicles/:id', async () => {
    mockAxiosInstance.delete.mockResolvedValue({});
    const { deleteVehicle } = await import('../api/vehicleApi');
    await deleteVehicle(5);
    expect(mockAxiosInstance.delete).toHaveBeenCalledWith('/api/vehicles/5');
  });

  it('purchaseVehicle calls POST /api/vehicles/:id/purchase', async () => {
    mockAxiosInstance.post.mockResolvedValue({ data: { quantity: 4 } });
    const { purchaseVehicle } = await import('../api/vehicleApi');
    const result = await purchaseVehicle(1, 2);
    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/vehicles/1/purchase', { quantity: 2 });
    expect(result.data.quantity).toBe(4);
  });

  it('restockVehicle calls POST /api/vehicles/:id/restock', async () => {
    mockAxiosInstance.post.mockResolvedValue({ data: { quantity: 10 } });
    const { restockVehicle } = await import('../api/vehicleApi');
    const result = await restockVehicle(1, 5);
    expect(mockAxiosInstance.post).toHaveBeenCalledWith('/api/vehicles/1/restock', { quantity: 5 });
    expect(result.data.quantity).toBe(10);
  });

  it('getVehicleById calls GET /api/vehicles/:id', async () => {
    mockAxiosInstance.get.mockResolvedValue({ data: { id: 1, make: 'Toyota' } });
    const { getVehicleById } = await import('../api/vehicleApi');
    const result = await getVehicleById(7);
    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/vehicles/7');
    expect(result.data.make).toBe('Toyota');
  });

  it('updateVehicle calls PUT /api/vehicles/:id', async () => {
    mockAxiosInstance.put.mockResolvedValue({ data: { id: 3, make: 'Honda' } });
    const { updateVehicle } = await import('../api/vehicleApi');
    await updateVehicle(3, { make: 'Honda', price: 25000 });
    expect(mockAxiosInstance.put).toHaveBeenCalledWith('/api/vehicles/3', { make: 'Honda', price: 25000 });
  });

  it('searchVehicles calls GET /api/vehicles/search', async () => {
    mockAxiosInstance.get.mockResolvedValue({ data: { content: [] } });
    const { searchVehicles } = await import('../api/vehicleApi');
    await searchVehicles({ make: 'Toyota', category: 'Sedan' });
    expect(mockAxiosInstance.get).toHaveBeenCalledWith('/api/vehicles/search', {
      params: { make: 'Toyota', category: 'Sedan' },
    });
  });
});
