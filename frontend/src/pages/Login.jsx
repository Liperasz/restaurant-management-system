import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const role = await login(email, password);
      showToast('Login realizado com sucesso!', 'success');
      if (role === 'CUSTOMER') navigate('/menu');
      else if (role === 'ATTENDANT') navigate('/attendant/orders');
      else if (role === 'ADMINISTRATOR') navigate('/admin/menu');
      else navigate('/');
    } catch (err) {
      setError('Credenciais inválidas. Tente novamente.');
      showToast('Credenciais inválidas. Tente novamente.', 'error');
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '0 auto', marginTop: '2rem' }}>
      <h2 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>Entrar no Camarada Camarão</h2>
      {error && <div style={{ color: 'var(--color-danger)', marginBottom: '1rem', padding: '0.5rem', backgroundColor: 'rgba(239, 68, 68, 0.1)', borderRadius: '4px' }}>{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Email</label>
          <input 
            type="email" 
            className="form-control" 
            value={email} 
            onChange={(e) => setEmail(e.target.value)} 
            required 
          />
        </div>
        <div className="form-group">
          <label>Senha</label>
          <input 
            type="password" 
            className="form-control" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)} 
            required 
          />
        </div>
        <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Entrar</button>
      </form>
    </div>
  );
};

export default Login;
