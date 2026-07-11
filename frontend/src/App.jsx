import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import VehicleDetail from './pages/VehicleDetail';
import AdminVehicles from './pages/AdminVehicles';
import CreateVehicle from './pages/CreateVehicle';
import EditVehicle from './pages/EditVehicle';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        <main className="main-content">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/" element={
              <ProtectedRoute><Dashboard /></ProtectedRoute>
            } />
            <Route path="/vehicles/:id" element={
              <ProtectedRoute><VehicleDetail /></ProtectedRoute>
            } />
            <Route path="/admin" element={
              <ProtectedRoute adminOnly><AdminVehicles /></ProtectedRoute>
            } />
            <Route path="/admin/vehicles/new" element={
              <ProtectedRoute adminOnly><CreateVehicle /></ProtectedRoute>
            } />
            <Route path="/admin/vehicles/:id/edit" element={
              <ProtectedRoute adminOnly><EditVehicle /></ProtectedRoute>
            } />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <Toaster position="top-right" richColors closeButton />
      </AuthProvider>
    </BrowserRouter>
  );
}
