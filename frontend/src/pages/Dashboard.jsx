import { useState, useEffect, useCallback } from 'react';
import { getAllVehicles, searchVehicles } from '../api/vehicleApi';
import VehicleCard from '../components/VehicleCard';
import SearchBar from '../components/SearchBar';
import Pagination from '../components/Pagination';

export default function Dashboard() {
  const [vehicles, setVehicles] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({});
  const [sort, setSort] = useState('make,asc');

  const fetchVehicles = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 12, sort };
      const hasFilters = filters.make || filters.model || filters.category
        || filters.minPrice || filters.maxPrice;

      const { data } = hasFilters
        ? await searchVehicles({ ...params, ...filters })
        : await getAllVehicles(params);

      setVehicles(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      console.error('Failed to fetch vehicles:', err);
    } finally {
      setLoading(false);
    }
  }, [page, sort, filters]);

  useEffect(() => {
    fetchVehicles();
  }, [fetchVehicles]);

  const handleSearch = (newFilters) => {
    setFilters(newFilters);
    setPage(0);
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Vehicle Inventory</h1>
        <div className="dashboard-controls">
          <select
            className="form-input sort-select"
            value={sort}
            onChange={(e) => { setSort(e.target.value); setPage(0); }}
          >
            <option value="make,asc">Make A-Z</option>
            <option value="make,desc">Make Z-A</option>
            <option value="price,asc">Price: Low to High</option>
            <option value="price,desc">Price: High to Low</option>
            <option value="quantity,asc">Stock: Low to High</option>
            <option value="quantity,desc">Stock: High to Low</option>
          </select>
        </div>
      </div>

      <SearchBar onSearch={handleSearch} />

      {loading ? (
        <div className="loading">Loading vehicles...</div>
      ) : vehicles.length === 0 ? (
        <div className="empty-state">
          <h2>No vehicles found</h2>
          <p>Try adjusting your search filters</p>
        </div>
      ) : (
        <>
          <p className="results-count">{totalElements} vehicle{totalElements !== 1 ? 's' : ''} found</p>
          <div className="vehicle-grid">
            {vehicles.map((v) => (
              <VehicleCard key={v.id} vehicle={v} />
            ))}
          </div>
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}
