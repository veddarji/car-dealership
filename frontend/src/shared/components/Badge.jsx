const variants = {
  success: 'badge-success',
  danger: 'badge-danger',
  warning: 'badge-warning',
  info: 'badge-info',
};

export default function Badge({ variant = 'info', children }) {
  return (
    <span className={`badge ${variants[variant] || variants.info}`}>
      {children}
    </span>
  );
}
