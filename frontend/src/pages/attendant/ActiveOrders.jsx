import React, { useState, useEffect } from 'react';
import api from '../../api/client';
import OrderCard from '../../components/OrderCard';

const ActiveOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchOrders = async () => {
    try {
      const response = await api.get('/orders');
      setOrders(response.data);
    } catch (error) {
      console.error('Failed to load orders', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
    const interval = setInterval(fetchOrders, 10000); // Poll every 10s
    return () => clearInterval(interval);
  }, []);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      await api.put(`/orders/${orderId}/status`, { status: newStatus });
      fetchOrders();
    } catch (error) {
      alert('Erro ao atualizar status do pedido.');
    }
  };

  if (loading) return <div>Carregando pedidos ativos...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Pedidos Ativos</h1>
      {orders.length === 0 ? (
        <p>Nenhum pedido ativo no momento.</p>
      ) : (
        <div className="card-grid">
          {orders.map(order => (
            <OrderCard key={order.id} order={order} showActions={true} onStatusChange={handleStatusChange} />
          ))}
        </div>
      )}
    </div>
  );
};

export default ActiveOrders;
