import { Link } from 'react-router-dom';

export default function VehicleCard({ vehicle }) {
  return (
    <div className="vehicle-card">
      <div className="vehicle-card-header">
        <h3>{vehicle.make} {vehicle.model}</h3>
        <span className="vehicle-category">{vehicle.category}</span>
      </div>
      <div className="vehicle-card-body">
        <div className="vehicle-price">
          ${vehicle.price.toLocaleString()}
        </div>
        <div className="vehicle-stock">
          <span className={`stock-indicator ${vehicle.quantity > 0 ? 'in-stock' : 'out-of-stock'}`} />
          {vehicle.quantity > 0 ? `${vehicle.quantity} in stock` : 'Out of stock'}
        </div>
      </div>
      <div className="vehicle-card-footer">
        <Link to={`/vehicles/${vehicle.id}`} className="btn btn-primary btn-sm">
          View Details
        </Link>
      </div>
    </div>
  );
}
