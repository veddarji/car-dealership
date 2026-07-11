import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from './features/auth/AuthContext';
import Navbar from './shared/components/Navbar';
import ProtectedRoute from './features/auth/ProtectedRoute';
import AdminRoute from './features/admin/AdminRoute';
import LoginPage from './features/auth/LoginPage';
import RegisterPage from './features/auth/RegisterPage';
import DashboardPage from './features/vehicles/DashboardPage';
import AdminPage from './features/admin/AdminPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route element={<Navbar />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route element={<ProtectedRoute />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
            </Route>

            <Route element={<AdminRoute />}>
              <Route path="/admin" element={<AdminPage />} />
            </Route>

            <Route path="*" element={
              <div className="empty-state" style={{ marginTop: 80 }}>
                <h1>404</h1>
                <p>Page not found</p>
                <a href="/dashboard" className="btn btn-primary" style={{ marginTop: 16, display: 'inline-block' }}>
                  Go to Dashboard
                </a>
              </div>
            } />
          </Route>
        </Routes>
        <Toaster position="top-right" richColors closeButton />
      </AuthProvider>
    </BrowserRouter>
  );
}
