import { useState } from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import Button from './Button';
import Badge from './Badge';

export default function Navbar() {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
      <nav className="navbar">
        <Link to="/dashboard" className="navbar-brand">AutoVault</Link>

        <button
          className={`hamburger ${menuOpen ? 'open' : ''}`}
          onClick={() => setMenuOpen(!menuOpen)}
          aria-label="Menu"
        >
          <span /><span /><span />
        </button>

        <div className={`navbar-links ${menuOpen ? 'visible' : ''}`}>
          <Link to="/dashboard" onClick={() => setMenuOpen(false)}>Dashboard</Link>
          {isAdmin && <Link to="/admin" onClick={() => setMenuOpen(false)}>Admin</Link>}
        </div>

        <div className={`navbar-user ${menuOpen ? 'visible' : ''}`}>
          {user ? (
            <>
              <span className="navbar-username">
                {user.username}
                <Badge variant={isAdmin ? 'danger' : 'info'}>{user.role}</Badge>
              </span>
              <Button variant="ghost" size="sm" onClick={handleLogout}>Logout</Button>
            </>
          ) : (
            <>
              <Link to="/login"><Button variant="ghost" size="sm">Login</Button></Link>
              <Link to="/register"><Button variant="primary" size="sm">Register</Button></Link>
            </>
          )}
        </div>
      </nav>
      <main className="main-content">
        <Outlet />
      </main>
    </>
  );
}
