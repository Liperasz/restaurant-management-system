import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../api/client';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = sessionStorage.getItem('authToken');
    const role = sessionStorage.getItem('userRole');
    const email = sessionStorage.getItem('userEmail');
    if (token && role && email) {
      setUser({ token, role, email });
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const token = btoa(`${email}:${password}`);
    // Temporarily set token for this request
    sessionStorage.setItem('authToken', token);
    try {
      const response = await api.get('/users/me');
      const role = response.data.role;
      sessionStorage.setItem('userRole', role);
      sessionStorage.setItem('userEmail', email);
      setUser({ token, role, email });
      return role;
    } catch (error) {
      sessionStorage.removeItem('authToken');
      throw error;
    }
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
