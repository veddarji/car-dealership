import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getVehicleById, deleteVehicle, purchaseVehicle, restockVehicle } from '../api/vehicleApi';
import { useAuth } from '../context/AuthContext';
import { toast } from 'sonner';

export default function VehicleDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [vehicle, setVehicle] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [action, setAction] = useState(null);

  useEffect(() => {
    getVehicleById(id)
      .then(({ data }) => setVehicle(data))
      .catch(() => {
        toast.error('Vehicle not found');
        navigate('/');
      })
      .finally(() => setLoading(false));
  }, [id, navigate]);

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
      navigate('/');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Delete failed');
    }
  };

  if (loading) return <div className="loading">Loading vehicle...</div>;
  if (!vehicle) return null;

  return (
    <div className="vehicle-detail">
      <button className="btn btn-outline" onClick={() => navigate('/')}>
        &larr; Back to Dashboard
      </button>

      <div className="detail-card">
        <div className="detail-header">
          <div>
            <h1>{vehicle.make} {vehicle.model}</h1>
            <span className="vehicle-category">{vehicle.category}</span>
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
            <span className={`stat-value ${vehicle.quantity > 0 ? 'text-success' : 'text-danger'}`}>
              {vehicle.quantity > 0 ? 'In Stock' : 'Out of Stock'}
            </span>
          </div>
        </div>

        <div className="detail-actions">
          <div className="quantity-control">
            <label htmlFor="quantity">Quantity:</label>
            <input
              id="quantity"
              type="number"
              className="form-input"
              min="1"
              max={vehicle.quantity}
              value={quantity}
              onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
            />
          </div>
          <div className="action-buttons">
            <button
              className="btn btn-primary"
              onClick={handlePurchase}
              disabled={action === 'purchase' || vehicle.quantity === 0}
            >
              {action === 'purchase' ? 'Processing...' : 'Purchase'}
            </button>
            {user?.role === 'ADMIN' && (
              <>
                <button
                  className="btn btn-success"
                  onClick={handleRestock}
                  disabled={action === 'restock'}
                >
                  {action === 'restock' ? 'Processing...' : 'Restock'}
                </button>
                <button className="btn btn-danger" onClick={handleDelete}>
                  Delete
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
