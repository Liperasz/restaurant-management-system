import React, { useState, useEffect } from 'react';
import api from '../../api/client';

const ManageStaff = () => {
  const [attendants, setAttendants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({ name: '', email: '', cpf: '', password: '', birthDate: '', phone: '', gender: 'MALE' });

  const fetchAttendants = async () => {
    try {
      const response = await api.get('/users/attendants');
      setAttendants(response.data);
    } catch (error) {
      console.error('Failed to load attendants', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAttendants();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/users/attendants', formData);
      setFormData({ name: '', email: '', cpf: '', password: '', birthDate: '', phone: '', gender: 'MALE' });
      fetchAttendants();
    } catch (error) {
      alert('Erro ao criar atendente.');
    }
  };

  if (loading) return <div>Carregando equipe...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Gerenciar Equipe</h1>
      
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h2 className="card-title">Novo Atendente</h2>
        <form onSubmit={handleSubmit} style={{ marginTop: '1rem' }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group"><label>Nome</label><input type="text" className="form-control" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} required /></div>
            <div className="form-group"><label>Email</label><input type="email" className="form-control" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} required /></div>
            <div className="form-group"><label>CPF</label><input type="text" className="form-control" value={formData.cpf} onChange={e => setFormData({...formData, cpf: e.target.value})} required /></div>
            <div className="form-group"><label>Senha</label><input type="password" className="form-control" value={formData.password} onChange={e => setFormData({...formData, password: e.target.value})} required /></div>
            <div className="form-group"><label>Data Nascimento</label><input type="date" className="form-control" value={formData.birthDate} onChange={e => setFormData({...formData, birthDate: e.target.value})} required /></div>
            <div className="form-group">
              <label>Gênero</label>
              <select className="form-control" value={formData.gender} onChange={e => setFormData({...formData, gender: e.target.value})}>
                <option value="MALE">Masculino</option>
                <option value="FEMALE">Feminino</option>
                <option value="OTHER">Outro</option>
              </select>
            </div>
          </div>
          <button type="submit" className="btn btn-primary" style={{ marginTop: '1rem' }}>Cadastrar Atendente</button>
        </form>
      </div>

      <div className="card-grid">
        {attendants.map(att => (
          <div key={att.id} className="card">
            <h3 className="card-title">{att.name}</h3>
            <p className="card-text">Email: {att.email}</p>
            <p className="card-text">CPF: {att.cpf}</p>
            <div className="card-footer">
              <button className="btn btn-danger" onClick={async () => {
                try {
                  await api.delete(`/users/attendants/${att.id}`);
                  fetchAttendants();
                } catch(e) { alert('Erro ao deletar'); }
              }}>Remover</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ManageStaff;
