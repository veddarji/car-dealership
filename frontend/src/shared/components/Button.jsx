import { forwardRef } from 'react';

const variants = {
  primary: 'btn-primary',
  danger: 'btn-danger',
  ghost: 'btn-ghost',
  outline: 'btn-outline',
  success: 'btn-success',
};

const sizes = {
  sm: 'btn-sm',
  md: '',
  lg: 'btn-lg',
};

const Button = forwardRef(({
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  fullWidth = false,
  className = '',
  children,
  ...props
}, ref) => {
  const cls = [
    'btn',
    variants[variant] || variants.primary,
    sizes[size] || '',
    fullWidth ? 'btn-block' : '',
    className,
  ].filter(Boolean).join(' ');

  return (
    <button
      ref={ref}
      className={cls}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <span className="spinner" />}
      {children}
    </button>
  );
});

Button.displayName = 'Button';
export default Button;
