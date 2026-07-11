export default function Loader({ text = 'Loading...' }) {
  return (
    <div className="loader">
      <div className="spinner-lg" />
      <p>{text}</p>
    </div>
  );
}

export function SkeletonCard() {
  return (
    <div className="skeleton-card">
      <div className="skeleton-line skeleton-title" />
      <div className="skeleton-line skeleton-text" />
      <div className="skeleton-line skeleton-text short" />
      <div className="skeleton-line skeleton-btn" />
    </div>
  );
}
