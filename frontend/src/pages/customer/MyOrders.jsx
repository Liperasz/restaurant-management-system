import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/client';
import OrderCard from '../../components/OrderCard';
import SkeletonCard from '../../components/SkeletonCard';

const MyOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await api.get('/orders/mine');
        setOrders(response.data);
      } catch (error) {
        console.error('Failed to load orders', error);
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  if (loading) {
    return (
      <div>
        <h1 style={{ marginBottom: '2rem' }}>Meus Pedidos</h1>
        <div className="card-grid">
          {Array.from({ length: 3 }).map((_, idx) => (
            <SkeletonCard key={idx} />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Meus Pedidos</h1>
      {orders.length === 0 ? (
        <p>Você ainda não fez nenhum pedido.</p>
      ) : (
        <div className="card-grid">
          {orders.map(order => (
            <div key={order.id} style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <OrderCard order={order} showActions={false} />
              {order.status === 'DELIVERED' && (
                <button className="btn btn-outline" onClick={() => navigate(`/feedback?orderId=${order.id}`)}>
                  Avaliar Pedido
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MyOrders;
