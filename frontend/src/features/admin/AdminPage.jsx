import { useState, useEffect, useCallback } from 'react';
import { createVehicle, updateVehicle, deleteVehicle, restockVehicle, searchVehicles } from '../../api/vehicleApi';
import VehicleGrid from '../vehicles/VehicleGrid';
import VehicleForm from '../vehicles/VehicleForm';
import Modal from '../../shared/components/Modal';
import Button from '../../shared/components/Button';
import Input from '../../shared/components/Input';
import { toast } from 'sonner';

export default function AdminPage() {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [formModal, setFormModal] = useState({ open: false, vehicle: null });
  const [restockModal, setRestockModal] = useState({ open: false, vehicle: null, qty: 1 });
  const [restocking, setRestocking] = useState(false);

  const fetchVehicles = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await searchVehicles({ size: 100, sort: 'make,asc' });
      setVehicles(data.content || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchVehicles(); }, [fetchVehicles]);

  const openCreate = () => setFormModal({ open: true, vehicle: null });

  const handleFormSubmit = async (data) => {
    if (formModal.vehicle) {
      const { data: updated } = await updateVehicle(formModal.vehicle.id, data);
      setVehicles((prev) => prev.map((v) => v.id === updated.id ? updated : v));
      toast.success('Vehicle updated');
    } else {
      const { data: created } = await createVehicle(data);
      setVehicles((prev) => [created, ...prev]);
      toast.success('Vehicle created');
    }
  };

  const handleDelete = async (vehicle) => {
    if (!window.confirm(`Delete ${vehicle.make} ${vehicle.model}?`)) return;
    try {
      await deleteVehicle(vehicle.id);
      setVehicles((prev) => prev.filter((v) => v.id !== vehicle.id));
      toast.success('Vehicle deleted');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Delete failed');
    }
  };

  const openRestock = (vehicle) => {
    setRestockModal({ open: true, vehicle, qty: 1 });
  };

  const handleRestock = async () => {
    setRestocking(true);
    try {
      const { data } = await restockVehicle(restockModal.vehicle.id, restockModal.qty);
      setVehicles((prev) => prev.map((v) => v.id === data.id ? data : v));
      toast.success(`Restocked ${restockModal.qty} vehicle(s)`);
      setRestockModal({ open: false, vehicle: null, qty: 1 });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Restock failed');
    } finally {
      setRestocking(false);
    }
  };

  return (
    <div className="admin-panel">
      <div className="admin-header">
        <h1>Admin Panel</h1>
        <Button onClick={openCreate}>+ Add Vehicle</Button>
      </div>

      <VehicleGrid
        vehicles={vehicles}
        loading={loading}
        error={error}
        onRetry={fetchVehicles}
        admin
        onPurchase={openRestock}
        onDelete={handleDelete}
      />

      <VehicleForm
        isOpen={formModal.open}
        onClose={() => setFormModal({ open: false, vehicle: null })}
        onSubmit={handleFormSubmit}
        initialData={formModal.vehicle}
        title={formModal.vehicle ? 'Edit Vehicle' : 'Add Vehicle'}
      />

      <Modal
        isOpen={restockModal.open}
        onClose={() => setRestockModal({ open: false, vehicle: null, qty: 1 })}
        title={`Restock ${restockModal.vehicle?.make} ${restockModal.vehicle?.model}`}
      >
        <p style={{ marginBottom: 12, color: 'var(--text-secondary)' }}>
          Current stock: <strong>{restockModal.vehicle?.quantity || 0}</strong>
        </p>
        <Input
          id="restock-qty"
          label="Quantity to add"
          type="number"
          min="1"
          value={restockModal.qty}
          onChange={(e) => setRestockModal({ ...restockModal, qty: Math.max(1, parseInt(e.target.value) || 1) })}
        />
        <div className="form-actions" style={{ marginTop: 16 }}>
          <Button variant="outline" onClick={() => setRestockModal({ open: false, vehicle: null, qty: 1 })}>
            Cancel
          </Button>
          <Button variant="success" onClick={handleRestock} loading={restocking}>
            Confirm Restock
          </Button>
        </div>
      </Modal>
    </div>
  );
}
