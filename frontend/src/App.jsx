import { BrowserRouter, Routes, Route, Navigate, Link } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from './features/auth/AuthContext';
import Navbar from './shared/components/Navbar';
import ProtectedRoute from './features/auth/ProtectedRoute';
import AdminRoute from './features/admin/AdminRoute';
import LoginPage from './features/auth/LoginPage';
import RegisterPage from './features/auth/RegisterPage';
import DashboardPage from './features/vehicles/DashboardPage';
import VehicleDetailPage from './features/vehicles/VehicleDetailPage';
import AdminPage from './features/admin/AdminPage';
import Button from './shared/components/Button';

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
              <Route path="/vehicles/:id" element={<VehicleDetailPage />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
            </Route>

            <Route element={<AdminRoute />}>
              <Route path="/admin" element={<AdminPage />} />
            </Route>

            <Route path="*" element={
              <div className="empty-state" style={{ marginTop: 80 }}>
                <h1 style={{ fontSize: '4rem', fontWeight: 800, marginBottom: 8, background: 'linear-gradient(135deg, var(--amber), var(--red))', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>404</h1>
                <p>Page not found</p>
                <Link to="/dashboard"><Button style={{ marginTop: 20 }}>Go to Dashboard</Button></Link>
              </div>
            } />
          </Route>
        </Routes>
        <Toaster position="top-right" richColors closeButton />
      </AuthProvider>
    </BrowserRouter>
  );
}
