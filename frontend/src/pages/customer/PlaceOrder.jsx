import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/client';
import MenuItemCard from '../../components/MenuItemCard';

const PlaceOrder = () => {
  const [items, setItems] = useState([]);
  const [cart, setCart] = useState({}); // { menuItemId: quantity }
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
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
    fetchMenu();
  }, []);

  const handleAdd = (item) => {
    setCart(prev => ({
      ...prev,
      [item.id]: (prev[item.id] || 0) + 1
    }));
  };

  const handleRemove = (item) => {
    setCart(prev => {
      const newCart = { ...prev };
      if (newCart[item.id] > 1) {
        newCart[item.id] -= 1;
      } else {
        delete newCart[item.id];
      }
      return newCart;
    });
  };

  const submitOrder = async () => {
    const orderItems = Object.keys(cart).map(id => ({
      menuItemId: parseInt(id),
      quantity: cart[id]
    }));
    
    if (orderItems.length === 0) return alert('Selecione pelo menos um item.');
    
    try {
      await api.post('/orders', { items: orderItems });
      alert('Pedido realizado com sucesso!');
      navigate('/order/mine');
    } catch (error) {
      alert('Erro ao realizar pedido.');
    }
  };

  if (loading) return <div>Carregando...</div>;

  const total = Object.keys(cart).reduce((sum, id) => {
    const item = items.find(i => i.id === parseInt(id));
    return sum + (item ? item.price * cart[id] : 0);
  }, 0);

  return (
    <div style={{ display: 'flex', gap: '2rem' }}>
      <div style={{ flex: 2 }}>
        <h1 style={{ marginBottom: '2rem' }}>Faça seu Pedido</h1>
        <div className="card-grid">
          {items.map(item => (
            <div key={item.id} className="card">
              <h3 className="card-title">{item.name}</h3>
              <p className="card-text">{item.description}</p>
              <div className="card-footer" style={{ flexDirection: 'column', alignItems: 'stretch', gap: '1rem' }}>
                <span className="price">{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.price)}</span>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <button className="btn btn-outline" onClick={() => handleRemove(item)}>-</button>
                  <span>{cart[item.id] || 0}</span>
                  <button className="btn btn-primary" onClick={() => handleAdd(item)}>+</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
      
      <div style={{ flex: 1, backgroundColor: 'var(--color-bg-card)', padding: '1.5rem', borderRadius: '8px', height: 'fit-content', position: 'sticky', top: '2rem' }}>
        <h2>Seu Carrinho</h2>
        <ul style={{ listStyleType: 'none', padding: 0, margin: '1.5rem 0' }}>
          {Object.keys(cart).map(id => {
            const item = items.find(i => i.id === parseInt(id));
            if (!item) return null;
            return (
              <li key={id} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                <span>{cart[id]}x {item.name}</span>
                <span>{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.price * cart[id])}</span>
              </li>
            );
          })}
        </ul>
        <div style={{ borderTop: '1px solid var(--color-border)', paddingTop: '1rem', marginTop: '1rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '1.25rem', fontWeight: 'bold', marginBottom: '1.5rem' }}>
            <span>Total:</span>
            <span>{new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(total)}</span>
          </div>
          <button className="btn btn-primary" style={{ width: '100%' }} onClick={submitOrder} disabled={total === 0}>
            Confirmar Pedido
          </button>
        </div>
      </div>
    </div>
  );
};

export default PlaceOrder;
