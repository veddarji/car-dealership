import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getVehicleById, updateVehicle } from '../api/vehicleApi';
import { toast } from 'sonner';

export default function EditVehicle() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    make: '', model: '', category: '', price: '', quantity: ''
  });
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(true);

  useEffect(() => {
    getVehicleById(id)
      .then(({ data }) => {
        setForm({
          make: data.make,
          model: data.model,
          category: data.category,
          price: data.price.toString(),
          quantity: data.quantity.toString(),
        });
      })
      .catch(() => {
        toast.error('Vehicle not found');
        navigate('/admin');
      })
      .finally(() => setFetching(false));
  }, [id, navigate]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await updateVehicle(id, {
        ...form,
        price: parseFloat(form.price),
        quantity: parseInt(form.quantity),
      });
      toast.success('Vehicle updated');
      navigate('/admin');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update vehicle');
    } finally {
      setLoading(false);
    }
  };

  if (fetching) return <div className="loading">Loading vehicle...</div>;

  return (
    <div className="vehicle-form-page">
      <h1>Edit Vehicle</h1>
      <form className="vehicle-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="make">Make</label>
          <input id="make" name="make" type="text" className="form-input" value={form.make} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label htmlFor="model">Model</label>
          <input id="model" name="model" type="text" className="form-input" value={form.model} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label htmlFor="category">Category</label>
          <input id="category" name="category" type="text" className="form-input" value={form.category} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label htmlFor="price">Price</label>
          <input id="price" name="price" type="number" className="form-input" value={form.price} onChange={handleChange} required min="0" step="0.01" />
        </div>
        <div className="form-group">
          <label htmlFor="quantity">Stock</label>
          <input id="quantity" name="quantity" type="number" className="form-input" value={form.quantity} onChange={handleChange} required min="0" />
        </div>
        <div className="form-actions">
          <button type="button" className="btn btn-outline" onClick={() => navigate('/admin')}>Cancel</button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Updating...' : 'Update Vehicle'}
          </button>
        </div>
      </form>
    </div>
  );
}
