import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

// Public Pages
// ... (omitting duplicate comments if any, but let's keep it complete)
import Login from './pages/Login';
import Register from './pages/Register';
import Menu from './pages/Menu';

// Customer Pages
import PlaceOrder from './pages/customer/PlaceOrder';
import MyOrders from './pages/customer/MyOrders';
import SubmitFeedback from './pages/customer/SubmitFeedback';

// Attendant Pages
import ActiveOrders from './pages/attendant/ActiveOrders';

// Admin Pages
import ManageMenu from './pages/admin/ManageMenu';
import ManageStock from './pages/admin/ManageStock';
import ManageStaff from './pages/admin/ManageStaff';
import Reports from './pages/admin/Reports';
import OrderHistory from './pages/admin/OrderHistory';
import Feedbacks from './pages/admin/Feedbacks';

function App() {
  return (
    <Router>
      <AuthProvider>
        <ToastProvider>
          <div className="app-container">
          <Navbar />
          <main className="main-content">
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<Navigate to="/menu" replace />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/menu" element={<Menu />} />

              {/* Customer Routes */}
              <Route element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
                <Route path="/order/new" element={<PlaceOrder />} />
                <Route path="/order/mine" element={<MyOrders />} />
                <Route path="/feedback" element={<SubmitFeedback />} />
              </Route>

              {/* Attendant & Admin Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ATTENDANT', 'ADMINISTRATOR']} />}>
                <Route path="/attendant/orders" element={<ActiveOrders />} />
              </Route>

              {/* Administrator Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ADMINISTRATOR']} />}>
                <Route path="/admin/menu" element={<ManageMenu />} />
                <Route path="/admin/stock" element={<ManageStock />} />
                <Route path="/admin/staff" element={<ManageStaff />} />
                <Route path="/admin/reports" element={<Reports />} />
                <Route path="/admin/history" element={<OrderHistory />} />
                <Route path="/admin/feedbacks" element={<Feedbacks />} />
              </Route>

              <Route path="*" element={<div>Página não encontrada</div>} />
            </Routes>
          </main>
        </div>
        </ToastProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
