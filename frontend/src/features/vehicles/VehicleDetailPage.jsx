import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { getVehicleById, deleteVehicle, purchaseVehicle, restockVehicle } from '../../api/vehicleApi';
import { useAuth } from '../../hooks/useAuth';
import Button from '../../shared/components/Button';
import Badge from '../../shared/components/Badge';
import Loader from '../../shared/components/Loader';
import { toast } from 'sonner';

export default function VehicleDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { isAdmin } = useAuth();
  const [vehicle, setVehicle] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [action, setAction] = useState(null);

  useEffect(() => {
    getVehicleById(id)
      .then(({ data }) => setVehicle(data))
      .catch(() => {
        toast.error('Vehicle not found');
        navigate('/dashboard');
      })
      .finally(() => setLoading(false));
  }, [id, navigate, location.key]);

  const handlePurchase = async () => {
    setAction('purchase');
    try {
      const { data } = await purchaseVehicle(id, quantity);
      setVehicle(data);
      toast.success(`Purchased ${quantity} vehicle(s)`);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Purchase failed');
    } finally {
      setAction(null);
    }
  };

  const handleRestock = async () => {
    setAction('restock');
    try {
      const { data } = await restockVehicle(id, quantity);
      setVehicle(data);
      toast.success(`Restocked ${quantity} vehicle(s)`);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Restock failed');
    } finally {
      setAction(null);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Delete this vehicle permanently?')) return;
    try {
      await deleteVehicle(id);
      toast.success('Vehicle deleted');
      navigate('/dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Delete failed');
    }
  };

  if (loading) return <Loader text="Loading vehicle..." />;
  if (!vehicle) return null;

  return (
    <div className="vehicle-detail">
      <Button variant="outline" onClick={() => navigate('/dashboard')}>
        &larr; Back to Dashboard
      </Button>

      <div className="detail-card glass">
        <div className="detail-header">
          <div>
            <h1>{vehicle.make} {vehicle.model}</h1>
            <Badge variant="info">{vehicle.category}</Badge>
          </div>
          <div className="detail-price">${vehicle.price.toLocaleString()}</div>
        </div>

        <div className="detail-stats">
          <div className="stat">
            <span className="stat-label">Stock</span>
            <span className={`stat-value ${vehicle.quantity > 0 ? 'text-success' : 'text-danger'}`}>
              {vehicle.quantity}
            </span>
          </div>
          <div className="stat">
            <span className="stat-label">Status</span>
            <Badge variant={vehicle.quantity > 0 ? 'success' : 'danger'}>
              {vehicle.quantity > 0 ? 'In Stock' : 'Out of Stock'}
            </Badge>
          </div>
        </div>

        <div className="detail-actions">
          <div className="quantity-control">
            <label htmlFor="detail-qty">Quantity:</label>
            <input
              id="detail-qty"
              type="number"
              className="form-input"
              min="1"
              max={vehicle.quantity || 1}
              value={quantity}
              onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
            />
          </div>
          <div className="action-buttons">
            <Button
              onClick={handlePurchase}
              disabled={action === 'purchase' || vehicle.quantity === 0}
              loading={action === 'purchase'}
            >
              Purchase
            </Button>
            {isAdmin && (
              <>
                <Button
                  variant="success"
                  onClick={handleRestock}
                  disabled={action === 'restock'}
                  loading={action === 'restock'}
                >
                  Restock
                </Button>
                <Button variant="danger" onClick={handleDelete}>Delete</Button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
