import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import SearchBar from '../features/vehicles/SearchBar';

const categories = ['Sedan', 'SUV', 'Coupe', 'Truck', 'Hatchback', 'Van'];

describe('SearchBar', () => {
  it('updates make field on user input', async () => {
    render(<SearchBar onSearch={vi.fn()} />);
    const input = screen.getByPlaceholderText('Make');
    await userEvent.type(input, 'Toyota');
    expect(input).toHaveValue('Toyota');
  });

  it('calls onSearch with correct params on submit', async () => {
    const onSearch = vi.fn();
    render(<SearchBar onSearch={onSearch} />);

    await userEvent.type(screen.getByPlaceholderText('Make'), 'Honda');
    await userEvent.type(screen.getByPlaceholderText('Model'), 'Civic');
    await userEvent.click(screen.getByRole('button', { name: /search/i }));

    expect(onSearch).toHaveBeenCalledWith(
      expect.objectContaining({ make: 'Honda', model: 'Civic', sort: 'make,asc' })
    );
  });

  it('clears all fields on clear button', async () => {
    const onSearch = vi.fn();
    render(<SearchBar onSearch={onSearch} />);

    await userEvent.type(screen.getByPlaceholderText('Make'), 'Toyota');
    await userEvent.click(screen.getByRole('button', { name: /clear/i }));

    expect(screen.getByPlaceholderText('Make')).toHaveValue('');
    expect(onSearch).toHaveBeenCalledWith({ sort: 'make,asc' });
  });

  it('shows category dropdown options', () => {
    render(<SearchBar onSearch={vi.fn()} />);
    const selects = screen.getAllByRole('combobox');
    expect(selects).toHaveLength(2);
    categories.forEach((cat) => {
      expect(screen.getByRole('option', { name: cat })).toBeInTheDocument();
    });
  });

  it('updates sort selection', async () => {
    const onSearch = vi.fn();
    render(<SearchBar onSearch={onSearch} />);
    const sortSelect = screen.getAllByRole('combobox')[1];
    await userEvent.selectOptions(sortSelect, 'price,desc');
    await userEvent.click(screen.getByRole('button', { name: /search/i }));
    expect(onSearch).toHaveBeenCalledWith(expect.objectContaining({ sort: 'price,desc' }));
  });
});
