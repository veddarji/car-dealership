import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">AutoVault</Link>
      </div>
      <div className="navbar-links">
        <Link to="/">Dashboard</Link>
        {user?.role === 'ADMIN' && <Link to="/admin">Admin Panel</Link>}
      </div>
      <div className="navbar-user">
        {user ? (
          <>
            <span className="navbar-username">
              {user.username}
              <span className={`role-badge role-${user.role.toLowerCase()}`}>
                {user.role}
              </span>
            </span>
            <button className="btn btn-outline btn-sm" onClick={handleLogout}>
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="btn btn-outline btn-sm">Login</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}
