import { useState, useEffect, useCallback } from 'react';
import { searchVehicles, purchaseVehicle } from '../../api/vehicleApi';
import VehicleGrid from './VehicleGrid';
import SearchBar from './SearchBar';
import Pagination from '../../shared/components/Pagination';
import Modal from '../../shared/components/Modal';
import Button from '../../shared/components/Button';
import Input from '../../shared/components/Input';
import { toast } from 'sonner';

export default function DashboardPage() {
  const [vehicles, setVehicles] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({});

  const [purchaseModal, setPurchaseModal] = useState({ open: false, vehicle: null, qty: 1 });
  const [purchasing, setPurchasing] = useState(false);

  const fetchVehicles = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = { page, size: 12, ...filters };
      const { data } = await searchVehicles(params);
      setVehicles(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load vehicles');
    } finally {
      setLoading(false);
    }
  }, [page, filters]);

  useEffect(() => { fetchVehicles(); }, [fetchVehicles]);

  const handleSearch = (newFilters) => {
    setFilters(newFilters);
    setPage(0);
  };

  const openPurchase = (vehicle) => {
    setPurchaseModal({ open: true, vehicle, qty: 1 });
  };

  const handlePurchase = async () => {
    setPurchasing(true);
    try {
      const { data } = await purchaseVehicle(purchaseModal.vehicle.id, purchaseModal.qty);
      setVehicles((prev) => prev.map((v) => v.id === data.id ? data : v));
      toast.success(`Purchased ${purchaseModal.qty} vehicle(s)`);
      setPurchaseModal({ open: false, vehicle: null, qty: 1 });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Purchase failed');
    } finally {
      setPurchasing(false);
    }
  };

  return (
    <div className="dashboard">
      <section className="hero">
        <h1>Discover Your Next Drive</h1>
        <p>Browse our inventory of premium vehicles</p>
      </section>

      <SearchBar onSearch={handleSearch} initialValues={filters} />

      {totalElements > 0 && (
        <p className="results-count">{totalElements} vehicle{totalElements !== 1 ? 's' : ''} found</p>
      )}

      <VehicleGrid
        vehicles={vehicles}
        loading={loading}
        error={error}
        onRetry={fetchVehicles}
        onPurchase={openPurchase}
      />

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />

      <Modal
        isOpen={purchaseModal.open}
        onClose={() => setPurchaseModal({ open: false, vehicle: null, qty: 1 })}
        title={`Purchase ${purchaseModal.vehicle?.make} ${purchaseModal.vehicle?.model}`}
      >
        <Input
          id="purchase-qty"
          label="Quantity"
          type="number"
          min="1"
          max={purchaseModal.vehicle?.quantity || 1}
          value={purchaseModal.qty}
          onChange={(e) => setPurchaseModal({ ...purchaseModal, qty: Math.max(1, parseInt(e.target.value) || 1) })}
        />
        <div className="form-actions" style={{ marginTop: 16 }}>
          <Button variant="outline" onClick={() => setPurchaseModal({ open: false, vehicle: null, qty: 1 })}>
            Cancel
          </Button>
          <Button onClick={handlePurchase} loading={purchasing}>
            Confirm Purchase
          </Button>
        </div>
      </Modal>
    </div>
  );
}
