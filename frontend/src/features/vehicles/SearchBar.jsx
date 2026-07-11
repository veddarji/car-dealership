import { useState } from 'react';
import Button from '../../shared/components/Button';
import Input from '../../shared/components/Input';

const categories = ['Sedan', 'SUV', 'Coupe', 'Truck', 'Hatchback', 'Van'];

export default function SearchBar({ onSearch, initialValues = {} }) {
  const [filters, setFilters] = useState({
    make: initialValues.make || '',
    model: initialValues.model || '',
    category: initialValues.category || '',
    minPrice: initialValues.minPrice || '',
    maxPrice: initialValues.maxPrice || '',
  });
  const [sort, setSort] = useState(initialValues.sort || 'make,asc');

  const handleChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const handleSearch = (e) => {
    e?.preventDefault();
    const params = {};
    Object.entries(filters).forEach(([k, v]) => {
      if (v) params[k] = v;
    });
    onSearch({ ...params, sort });
  };

  const handleClear = () => {
    setFilters({ make: '', model: '', category: '', minPrice: '', maxPrice: '' });
    setSort('make,asc');
    onSearch({ sort: 'make,asc' });
  };

  return (
    <form className="search-bar glass" onSubmit={handleSearch}>
      <div className="search-grid">
        <Input
          id="search-make"
          placeholder="Make"
          name="make"
          value={filters.make}
          onChange={handleChange}
        />
        <Input
          id="search-model"
          placeholder="Model"
          name="model"
          value={filters.model}
          onChange={handleChange}
        />
        <select
          name="category"
          className="form-input"
          value={filters.category}
          onChange={handleChange}
        >
          <option value="">All Categories</option>
          {categories.map((c) => <option key={c} value={c}>{c}</option>)}
        </select>
        <input
          type="number"
          placeholder="Min Price"
          name="minPrice"
          className="form-input"
          value={filters.minPrice}
          onChange={handleChange}
          min="0"
        />
        <input
          type="number"
          placeholder="Max Price"
          name="maxPrice"
          className="form-input"
          value={filters.maxPrice}
          onChange={handleChange}
          min="0"
        />
        <select
          className="form-input"
          value={sort}
          onChange={(e) => setSort(e.target.value)}
        >
          <option value="make,asc">Name A-Z</option>
          <option value="make,desc">Name Z-A</option>
          <option value="price,asc">Price: Low to High</option>
          <option value="price,desc">Price: High to Low</option>
        </select>
      </div>
      <div className="search-actions">
        <Button type="submit">Search</Button>
        <Button type="button" variant="outline" onClick={handleClear}>Clear</Button>
      </div>
    </form>
  );
}
