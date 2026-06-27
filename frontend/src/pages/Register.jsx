import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    cpf: '',
    phone: '',
    password: '',
    birthDate: '',
    gender: 'MALE'
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/users/register', formData);
      navigate('/login');
    } catch (err) {
      if (err.response?.data?.error) {
        setError(err.response.data.error);
      } else {
        setError('Erro ao registrar. Verifique os dados e tente novamente.');
      }
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto', marginTop: '2rem' }}>
      <h2 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>Cadastre-se</h2>
      {error && <div style={{ color: 'var(--color-danger)', marginBottom: '1rem', padding: '0.5rem', backgroundColor: 'rgba(239, 68, 68, 0.1)', borderRadius: '4px' }}>{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nome Completo</label>
          <input type="text" name="name" className="form-control" value={formData.name} onChange={handleChange} required />
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" name="email" className="form-control" value={formData.email} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>CPF (Apenas números)</label>
            <input type="text" name="cpf" className="form-control" maxLength="11" value={formData.cpf} onChange={handleChange} required />
          </div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Telefone</label>
            <input type="text" name="phone" className="form-control" value={formData.phone} onChange={handleChange} />
          </div>
          <div className="form-group">
            <label>Data de Nascimento</label>
            <input type="date" name="birthDate" className="form-control" value={formData.birthDate} onChange={handleChange} required />
          </div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label>Gênero</label>
            <select name="gender" className="form-control" value={formData.gender} onChange={handleChange}>
              <option value="MALE">Masculino</option>
              <option value="FEMALE">Feminino</option>
              <option value="OTHER">Outro</option>
            </select>
          </div>
          <div className="form-group">
            <label>Senha</label>
            <input type="password" name="password" className="form-control" value={formData.password} onChange={handleChange} required minLength="6" />
          </div>
        </div>
        <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }}>Concluir Cadastro</button>
      </form>
    </div>
  );
};

export default Register;
