import { useState, useEffect } from 'react';
import Button from '../../shared/components/Button';
import Input from '../../shared/components/Input';

const categories = ['Sedan', 'SUV', 'Coupe', 'Truck', 'Hatchback', 'Van'];

const emptyForm = { make: '', model: '', category: '', price: '', quantity: '' };

export default function VehicleForm({ isOpen, onClose, onSubmit, initialData, title }) {
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setForm(initialData ? {
        make: initialData.make,
        model: initialData.model,
        category: initialData.category,
        price: initialData.price.toString(),
        quantity: initialData.quantity.toString(),
      } : { ...emptyForm });
      setErrors({});
    }
  }, [isOpen, initialData]);

  const validate = () => {
    const errs = {};
    if (!form.make.trim()) errs.make = 'Required';
    if (!form.model.trim()) errs.model = 'Required';
    if (!form.category) errs.category = 'Required';
    if (!form.price || parseFloat(form.price) <= 0) errs.price = 'Must be > 0';
    if (form.quantity === '' || parseInt(form.quantity) < 0) errs.quantity = 'Must be >= 0';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    try {
      await onSubmit({
        make: form.make.trim(),
        model: form.model.trim(),
        category: form.category,
        price: parseFloat(form.price),
        quantity: parseInt(form.quantity),
      });
      onClose();
    } catch (err) {
      // error handled by parent
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{title || (initialData ? 'Edit Vehicle' : 'Add Vehicle')}</h2>
          <button className="modal-close" onClick={onClose}>&times;</button>
        </div>
        <form className="modal-body vehicle-form" onSubmit={handleSubmit}>
          <Input
            id="vf-make"
            label="Make"
            value={form.make}
            onChange={(e) => setForm({ ...form, make: e.target.value })}
            error={errors.make}
          />
          <Input
            id="vf-model"
            label="Model"
            value={form.model}
            onChange={(e) => setForm({ ...form, model: e.target.value })}
            error={errors.model}
          />
          <div className="form-group">
            <label htmlFor="vf-category">Category</label>
            <select
              id="vf-category"
              className={`form-input ${errors.category ? 'input-error' : ''}`}
              value={form.category}
              onChange={(e) => setForm({ ...form, category: e.target.value })}
            >
              <option value="">Select category</option>
              {categories.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
            {errors.category && <span className="form-error">{errors.category}</span>}
          </div>
          <Input
            id="vf-price"
            label="Price"
            type="number"
            min="0"
            step="0.01"
            value={form.price}
            onChange={(e) => setForm({ ...form, price: e.target.value })}
            error={errors.price}
          />
          <Input
            id="vf-quantity"
            label="Quantity"
            type="number"
            min="0"
            value={form.quantity}
            onChange={(e) => setForm({ ...form, quantity: e.target.value })}
            error={errors.quantity}
          />
          <div className="form-actions">
            <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
            <Button type="submit" loading={loading}>
              {initialData ? 'Update' : 'Create'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
