import React, { useState, useEffect } from 'react';
import api from '../api/client';
import MenuItemCard from '../components/MenuItemCard';

const Menu = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

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

  if (loading) return <div>Carregando cardápio...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Nosso Cardápio</h1>
      <div className="card-grid">
        {items.map(item => (
          <MenuItemCard key={item.id} item={item} />
        ))}
      </div>
    </div>
  );
};

export default Menu;
