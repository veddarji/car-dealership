import { forwardRef } from 'react';

const Input = forwardRef(({ label, error, className = '', ...props }, ref) => {
  return (
    <div className={`form-group ${error ? 'has-error' : ''} ${className}`}>
      {label && <label htmlFor={props.id}>{label}</label>}
      <input
        ref={ref}
        className={`form-input ${error ? 'input-error' : ''}`}
        {...props}
      />
      {error && <span className="form-error">{error}</span>}
    </div>
  );
});

Input.displayName = 'Input';
export default Input;
