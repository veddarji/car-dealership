import { createContext, useContext, useState, useCallback, useEffect } from 'react';
import { login as apiLogin, register as apiRegister } from '../api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const username = localStorage.getItem('username');
    const role = localStorage.getItem('role');
    const token = localStorage.getItem('token');
    return username && token ? { username, role, token } : null;
  });

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

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
