import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import userEvent from '@testing-library/user-event';
import Button from '../shared/components/Button';

describe('Button', () => {
  it('renders children', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByRole('button', { name: /click me/i })).toBeInTheDocument();
  });

  it('applies primary variant by default', () => {
    render(<Button>Primary</Button>);
    expect(screen.getByText('Primary').className).toContain('btn-primary');
  });

  it('applies danger variant', () => {
    render(<Button variant="danger">Delete</Button>);
    expect(screen.getByText('Delete').className).toContain('btn-danger');
  });

  it('applies outline variant', () => {
    render(<Button variant="outline">Cancel</Button>);
    expect(screen.getByText('Cancel').className).toContain('btn-outline');
  });

  it('applies success variant', () => {
    render(<Button variant="success">Save</Button>);
    expect(screen.getByText('Save').className).toContain('btn-success');
  });

  it('applies ghost variant', () => {
    render(<Button variant="ghost">Ghost</Button>);
    expect(screen.getByText('Ghost').className).toContain('btn-ghost');
  });

  it('falls back to primary for unknown variant', () => {
    render(<Button variant="invalid">Test</Button>);
    expect(screen.getByText('Test').className).toContain('btn-primary');
  });

  it('applies sm size class', () => {
    render(<Button size="sm">Small</Button>);
    expect(screen.getByText('Small').className).toContain('btn-sm');
  });

  it('applies lg size class', () => {
    render(<Button size="lg">Large</Button>);
    expect(screen.getByText('Large').className).toContain('btn-lg');
  });

  it('shows spinner when loading', () => {
    render(<Button loading>Saving</Button>);
    expect(screen.getByText('Saving').querySelector('.spinner')).toBeInTheDocument();
  });

  it('is disabled when loading', () => {
    render(<Button loading>Save</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('is disabled when disabled prop is true', () => {
    render(<Button disabled>Save</Button>);
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('applies block class when fullWidth', () => {
    render(<Button fullWidth>Full</Button>);
    expect(screen.getByText('Full').className).toContain('btn-block');
  });

  it('merges custom className', () => {
    render(<Button className="custom-class">Custom</Button>);
    expect(screen.getByText('Custom').className).toContain('custom-class');
  });

  it('fires onClick handler', async () => {
    const onClick = vi.fn();
    render(<Button onClick={onClick}>Click</Button>);
    await userEvent.click(screen.getByRole('button', { name: /click/i }));
    expect(onClick).toHaveBeenCalled();
  });
});
