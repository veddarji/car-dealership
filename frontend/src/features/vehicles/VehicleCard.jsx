import { Link } from 'react-router-dom';
import Button from '../../shared/components/Button';
import Badge from '../../shared/components/Badge';
import { useAuth } from '../../hooks/useAuth';

const categoryGradients = {
  Sedan: 'linear-gradient(135deg, #667eea, #764ba2)',
  SUV: 'linear-gradient(135deg, #f093fb, #f5576c)',
  Coupe: 'linear-gradient(135deg, #4facfe, #00f2fe)',
  Truck: 'linear-gradient(135deg, #43e97b, #38f9d7)',
  Hatchback: 'linear-gradient(135deg, #fa709a, #fee140)',
  Van: 'linear-gradient(135deg, #a18cd1, #fbc2eb)',
};

export default function VehicleCard({ vehicle, onPurchase, onDelete, admin = false }) {
  const { isAdmin } = useAuth();
  const gradient = categoryGradients[vehicle.category] || categoryGradients.Sedan;
  const inStock = vehicle.quantity > 0;

  return (
    <div className="vehicle-card glass">
      <div className="vehicle-card-img" style={{ background: gradient }}>
        <div className="vehicle-card-badge">
          <Badge variant={inStock ? 'success' : 'danger'}>
            {inStock ? `${vehicle.quantity} in stock` : 'Out of stock'}
          </Badge>
        </div>
      </div>
      <div className="vehicle-card-body">
        <div className="vehicle-card-top">
          <h3>{vehicle.make} {vehicle.model}</h3>
          <Badge variant="info">{vehicle.category}</Badge>
        </div>
        <div className="vehicle-price">${vehicle.price.toLocaleString()}</div>
        <div className="vehicle-card-actions">
          {admin ? (
            <>
              <Button
                size="sm"
                variant="success"
                onClick={() => onPurchase?.(vehicle)}
                disabled={!inStock}
              >
                Restock
              </Button>
              <Button size="sm" variant="danger" onClick={() => onDelete?.(vehicle)}>
                Delete
              </Button>
            </>
          ) : (
            <>
              <Button
                size="sm"
                variant="primary"
                onClick={() => onPurchase?.(vehicle)}
                disabled={!inStock}
              >
                {inStock ? 'Purchase' : 'Out of Stock'}
              </Button>
              <Link to={`/vehicles/${vehicle.id}`} className="btn btn-outline btn-sm">
                Details
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
