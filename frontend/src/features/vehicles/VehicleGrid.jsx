import VehicleCard from './VehicleCard';
import { SkeletonCard } from '../../shared/components/Loader';
import Button from '../../shared/components/Button';

export default function VehicleGrid({ vehicles, loading, error, onRetry, admin = false, onPurchase, onDelete }) {
  if (error) {
    return (
      <div className="empty-state">
        <h2>Something went wrong</h2>
        <p>{error}</p>
          {onRetry && (
            <Button onClick={onRetry}>Retry</Button>
          )}
      </div>
    );
  }

  if (loading) {
    return (
      <div className="vehicle-grid">
        {[1, 2, 3].map((i) => <SkeletonCard key={i} />)}
      </div>
    );
  }

  if (!vehicles || vehicles.length === 0) {
    return (
      <div className="empty-state">
        <h2>No vehicles found</h2>
        <p>Try adjusting your search filters</p>
      </div>
    );
  }

  return (
    <div className="vehicle-grid">
      {vehicles.map((v) => (
        <VehicleCard
          key={v.id}
          vehicle={v}
          admin={admin}
          onPurchase={onPurchase}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
