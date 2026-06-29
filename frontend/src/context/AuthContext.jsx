import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../api/client';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser]       = useState(null);
  const [loading, setLoading] = useState(true);

  // On mount: restore session from sessionStorage if token exists
  useEffect(() => {
    const token = sessionStorage.getItem('authToken');
    const role  = sessionStorage.getItem('userRole');
    const email = sessionStorage.getItem('userEmail');
    if (token && role && email) {
      setUser({ token, role, email });
    }
    setLoading(false);
  }, []);

  /**
   * Login flow (JWT):
   *  1. POST /api/auth/login with { email, password }
   *  2. Receive { token, role, email } from the server
   *  3. Store token in sessionStorage
   *  4. All subsequent api.* calls will inject "Authorization: Bearer <token>"
   */
  const login = async (email, password) => {
    // Call the dedicated auth endpoint — no credentials stored before validation
    const response = await api.post('/auth/login', { email, password });
    const { token, role } = response.data;

    sessionStorage.setItem('authToken', token);
    sessionStorage.setItem('userRole', role);
    sessionStorage.setItem('userEmail', email);

    setUser({ token, role, email });
    return role;
  };

  const logout = () => {
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('userRole');
    sessionStorage.removeItem('userEmail');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
