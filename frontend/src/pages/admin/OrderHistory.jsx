import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import OrderCard from '../../components/OrderCard';

const OrderHistory = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await api.get('/orders/history');
        setOrders(response.data);
      } catch (error) {
        console.error('Failed to load order history', error);
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

  if (loading) return <div>Carregando histórico...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Histórico de Pedidos</h1>
      {orders.length === 0 ? (
        <p>Nenhum pedido encontrado.</p>
      ) : (
        <div className="card-grid">
          {orders.map(order => (
            <OrderCard key={order.id} order={order} showActions={false} />
          ))}
        </div>
      )}
    </div>
  );
};

export default OrderHistory;
