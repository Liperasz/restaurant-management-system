import React, { useState, useEffect } from 'react';
import api from '../../api/client';

const ManageStock = () => {
  const [stock, setStock] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({ ingredientId: '', quantity: '', batch: '', expirationDate: '' });
  const [ingredients, setIngredients] = useState([]);

  const fetchData = async () => {
    try {
      const [stockRes, ingRes] = await Promise.all([
        api.get('/stock'),
        api.get('/ingredients')
      ]);
      setStock(stockRes.data);
      setIngredients(ingRes.data);
      if (ingRes.data.length > 0) {
        setFormData(prev => ({ ...prev, ingredientId: ingRes.data[0].id }));
      }
    } catch (error) {
      console.error('Failed to load data', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/stock', {
        ingredientId: parseInt(formData.ingredientId),
        quantity: parseFloat(formData.quantity),
        batch: formData.batch,
        expirationDate: formData.expirationDate
      });
      setFormData({ ...formData, quantity: '', batch: '', expirationDate: '' });
      fetchData();
    } catch (error) {
      alert('Erro ao adicionar lote de estoque.');
    }
  };

  if (loading) return <div>Carregando estoque...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Gerenciar Estoque</h1>
      
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h2 className="card-title">Adicionar Lote</h2>
        <form onSubmit={handleSubmit} style={{ marginTop: '1rem', display: 'grid', gridTemplateColumns: '1fr 1fr 1fr 1fr auto', gap: '1rem', alignItems: 'end' }}>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Ingrediente</label>
            <select className="form-control" value={formData.ingredientId} onChange={e => setFormData({...formData, ingredientId: e.target.value})}>
              {ingredients.map(ing => (
                <option key={ing.id} value={ing.id}>{ing.name} ({ing.unit})</option>
              ))}
            </select>
          </div>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Quantidade</label>
            <input type="number" step="0.01" className="form-control" value={formData.quantity} onChange={e => setFormData({...formData, quantity: e.target.value})} required />
          </div>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Lote (Batch)</label>
            <input type="text" className="form-control" value={formData.batch} onChange={e => setFormData({...formData, batch: e.target.value})} required />
          </div>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Data de Validade</label>
            <input type="date" className="form-control" value={formData.expirationDate} onChange={e => setFormData({...formData, expirationDate: e.target.value})} required />
          </div>
          <button type="submit" className="btn btn-primary">Adicionar</button>
        </form>
      </div>

      <div className="card-grid">
        {stock.map(item => (
          <div key={item.id} className="card">
            <h3 className="card-title">{item.ingredient.name}</h3>
            <p className="card-text">Lote: {item.batch}</p>
            <p className="card-text">Qtd: {item.quantity} {item.ingredient.unit}</p>
            <p className="card-text" style={{ color: new Date(item.expirationDate) < new Date() ? 'var(--color-danger)' : 'inherit' }}>
              Validade: {new Date(item.expirationDate).toLocaleDateString('pt-BR')}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ManageStock;
