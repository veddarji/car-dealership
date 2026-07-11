import { useState } from 'react';

export default function SearchBar({ onSearch, initialValues = {} }) {
  const [make, setMake] = useState(initialValues.make || '');
  const [model, setModel] = useState(initialValues.model || '');
  const [category, setCategory] = useState(initialValues.category || '');
  const [minPrice, setMinPrice] = useState(initialValues.minPrice || '');
  const [maxPrice, setMaxPrice] = useState(initialValues.maxPrice || '');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch({
      make: make || undefined,
      model: model || undefined,
      category: category || undefined,
      minPrice: minPrice || undefined,
      maxPrice: maxPrice || undefined,
    });
  };

  const handleReset = () => {
    setMake('');
    setModel('');
    setCategory('');
    setMinPrice('');
    setMaxPrice('');
    onSearch({});
  };

  return (
    <form className="search-bar" onSubmit={handleSubmit}>
      <div className="search-grid">
        <input
          type="text"
          placeholder="Make"
          value={make}
          onChange={(e) => setMake(e.target.value)}
          className="form-input"
        />
        <input
          type="text"
          placeholder="Model"
          value={model}
          onChange={(e) => setModel(e.target.value)}
          className="form-input"
        />
        <input
          type="text"
          placeholder="Category"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className="form-input"
        />
        <input
          type="number"
          placeholder="Min Price"
          value={minPrice}
          onChange={(e) => setMinPrice(e.target.value)}
          className="form-input"
          min="0"
        />
        <input
          type="number"
          placeholder="Max Price"
          value={maxPrice}
          onChange={(e) => setMaxPrice(e.target.value)}
          className="form-input"
          min="0"
        />
      </div>
      <div className="search-actions">
        <button type="submit" className="btn btn-primary">Search</button>
        <button type="button" className="btn btn-outline" onClick={handleReset}>Clear</button>
      </div>
    </form>
  );
}
