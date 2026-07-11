import { createContext, useState, useCallback, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from '../../api/authApi';
import Loader from '../../shared/components/Loader';

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    if (token && username) {
      setUser({ username, role, token });
    }
    setLoading(false);
  }, []);

  const login = useCallback(async (credentials) => {
    const { data } = await apiLogin(credentials);
    localStorage.setItem('token', data.token);
    localStorage.setItem('username', data.username);
    localStorage.setItem('role', data.role);
    setUser({ username: data.username, role: data.role, token: data.token });
    return data;
  }, []);

  const register = useCallback(async (credentials) => {
    const { data } = await apiRegister(credentials);
    localStorage.setItem('token', data.token);
    localStorage.setItem('username', data.username);
    localStorage.setItem('role', data.role);
    setUser({ username: data.username, role: data.role, token: data.token });
    return data;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    setUser(null);
  }, []);

  if (loading) return <Loader text="Checking authentication..." />;

  return (
    <AuthContext.Provider value={{ user, loading, isAdmin: user?.role === 'ADMIN', login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
