import Button from './Button';

export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const pages = [];
  const start = Math.max(0, page - 2);
  const end = Math.min(totalPages - 1, page + 2);
  for (let i = start; i <= end; i++) pages.push(i);

  return (
    <div className="pagination">
      <Button variant="outline" size="sm" disabled={page === 0} onClick={() => onPageChange(0)}>
        First
      </Button>
      <Button variant="outline" size="sm" disabled={page === 0} onClick={() => onPageChange(page - 1)}>
        Prev
      </Button>
      {pages.map((p) => (
        <Button
          key={p}
          variant={p === page ? 'primary' : 'outline'}
          size="sm"
          onClick={() => onPageChange(p)}
        >
          {p + 1}
        </Button>
      ))}
      <Button variant="outline" size="sm" disabled={page >= totalPages - 1} onClick={() => onPageChange(page + 1)}>
        Next
      </Button>
      <Button variant="outline" size="sm" disabled={page >= totalPages - 1} onClick={() => onPageChange(totalPages - 1)}>
        Last
      </Button>
    </div>
  );
}
