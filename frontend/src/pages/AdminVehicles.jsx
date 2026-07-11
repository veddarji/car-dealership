import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { getAllVehicles, deleteVehicle } from '../api/vehicleApi';
import Pagination from '../components/Pagination';
import { toast } from 'sonner';

export default function AdminVehicles() {
  const [vehicles, setVehicles] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);

  const fetchVehicles = useCallback(async () => {
    setLoading(true);
    try {
      const { data } = await getAllVehicles({ page, size: 20, sort: 'make,asc' });
      setVehicles(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      toast.error('Failed to load vehicles');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => { fetchVehicles(); }, [fetchVehicles]);

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Delete ${name}?`)) return;
    try {
      await deleteVehicle(id);
      toast.success('Vehicle deleted');
      fetchVehicles();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Delete failed');
    }
  };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="admin-panel">
      <div className="admin-header">
        <h1>Admin Panel</h1>
        <Link to="/admin/vehicles/new" className="btn btn-primary">+ Add Vehicle</Link>
      </div>

      <table className="admin-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Make</th>
            <th>Model</th>
            <th>Category</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {vehicles.map((v) => (
            <tr key={v.id}>
              <td>{v.id}</td>
              <td>{v.make}</td>
              <td>{v.model}</td>
              <td><span className="vehicle-category">{v.category}</span></td>
              <td>${v.price.toLocaleString()}</td>
              <td>
                <span className={`stock-indicator ${v.quantity > 0 ? 'in-stock' : 'out-of-stock'}`} />
                {v.quantity}
              </td>
              <td className="action-cell">
                <Link to={`/admin/vehicles/${v.id}/edit`} className="btn btn-outline btn-sm">Edit</Link>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleDelete(v.id, `${v.make} ${v.model}`)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}
