import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import Badge from '../shared/components/Badge';

describe('Badge', () => {
  it('renders with success variant', () => {
    render(<Badge variant="success">In Stock</Badge>);
    const badge = screen.getByText('In Stock');
    expect(badge.className).toContain('badge-success');
  });

  it('renders with danger variant', () => {
    render(<Badge variant="danger">Out of Stock</Badge>);
    const badge = screen.getByText('Out of Stock');
    expect(badge.className).toContain('badge-danger');
  });

  it('renders with warning variant', () => {
    render(<Badge variant="warning">Low Stock</Badge>);
    const badge = screen.getByText('Low Stock');
    expect(badge.className).toContain('badge-warning');
  });

  it('renders with info variant', () => {
    render(<Badge variant="info">Sedan</Badge>);
    const badge = screen.getByText('Sedan');
    expect(badge.className).toContain('badge-info');
  });

  it('falls back to info for unknown variant', () => {
    render(<Badge variant="unknown">Test</Badge>);
    const badge = screen.getByText('Test');
    expect(badge.className).toContain('badge-info');
  });

  it('uses info as default variant', () => {
    render(<Badge>Default</Badge>);
    const badge = screen.getByText('Default');
    expect(badge.className).toContain('badge-info');
  });
});
