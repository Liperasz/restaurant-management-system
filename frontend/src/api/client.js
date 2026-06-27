import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});

// Request interceptor to add Basic Auth header
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Basic ${token}`;
  }
  return config;
});

// Response interceptor to handle 401 Unauthorized
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('userRole');
      sessionStorage.removeItem('userEmail');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
