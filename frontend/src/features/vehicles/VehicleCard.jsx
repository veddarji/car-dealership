import { useRef } from 'react';
import { Link } from 'react-router-dom';
import Button from '../../shared/components/Button';
import Badge from '../../shared/components/Badge';
import { getVehicleImage, getVehicleGradient } from '../../utils/vehicleImages';

export default function VehicleCard({ vehicle, onPurchase, onDelete, admin = false }) {
  const cardRef = useRef(null);
  const inStock = vehicle.quantity > 0;
  const imgUrl = getVehicleImage(vehicle.category);
  const gradient = getVehicleGradient(vehicle.category);

  const handleMouseMove = (e) => {
    const card = cardRef.current;
    if (!card) return;
    const rect = card.getBoundingClientRect();
    const x = (e.clientX - rect.left) / rect.width - 0.5;
    const y = (e.clientY - rect.top) / rect.height - 0.5;
    card.style.transform = `perspective(800px) rotateY(${x * 12}deg) rotateX(${y * -8}deg) translateY(-4px)`;
  };

  const handleMouseLeave = () => {
    if (cardRef.current) {
      cardRef.current.style.transform = 'perspective(800px) rotateY(0deg) rotateX(0deg) translateY(0)';
    }
  };

  return (
    <div className="vehicle-card" ref={cardRef} onMouseMove={handleMouseMove} onMouseLeave={handleMouseLeave}>
      <div className="vehicle-card-img" style={{ background: gradient }}>
        <img src={imgUrl} alt={vehicle.category} className="vehicle-card-img-el" loading="lazy" />
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
              <Link to={`/vehicles/${vehicle.id}`}>
                <Button variant="outline" size="sm">Details</Button>
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
