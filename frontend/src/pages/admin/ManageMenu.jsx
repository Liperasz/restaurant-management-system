import React, { useState, useEffect } from 'react';
import api from '../../api/client';

const ManageMenu = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({ name: '', description: '', price: '' });

  const fetchMenu = async () => {
    try {
      const response = await api.get('/menu');
      setItems(response.data);
    } catch (error) {
      console.error('Failed to load menu', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMenu();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/menu', {
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price),
        ingredientIds: [] // Placeholder
      });
      setFormData({ name: '', description: '', price: '' });
      fetchMenu();
    } catch (error) {
      alert('Erro ao criar item do cardápio.');
    }
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`/menu/${id}`);
      fetchMenu();
    } catch (error) {
      alert('Erro ao remover item.');
    }
  };

  if (loading) return <div>Carregando cardápio...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Gerenciar Cardápio</h1>
      
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h2 className="card-title">Adicionar Novo Item</h2>
        <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr auto', gap: '1rem', alignItems: 'end', marginTop: '1rem' }}>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Nome</label>
            <input type="text" className="form-control" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} required />
          </div>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Descrição</label>
            <input type="text" className="form-control" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} required />
          </div>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Preço</label>
            <input type="number" step="0.01" className="form-control" value={formData.price} onChange={e => setFormData({...formData, price: e.target.value})} required />
          </div>
          <button type="submit" className="btn btn-primary">Adicionar</button>
        </form>
      </div>

      <div className="card-grid">
        {items.map(item => (
          <div key={item.id} className="card">
            <h3 className="card-title">{item.name}</h3>
            <p className="card-text">{item.description}</p>
            <div className="card-footer" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span className="price">{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.price)}</span>
              <button className="btn btn-danger" onClick={() => handleDelete(item.id)}>Desativar</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ManageMenu;
