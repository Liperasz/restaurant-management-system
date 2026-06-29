import React, { useState, useEffect } from 'react';
import api from '../../api/client';

const Feedbacks = () => {
  const [feedbacks, setFeedbacks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFeedbacks = async () => {
      try {
        const response = await api.get('/feedbacks');
        setFeedbacks(response.data);
      } catch (error) {
        console.error('Failed to load feedbacks', error);
      } finally {
        setLoading(false);
      }
    };
    fetchFeedbacks();
  }, []);

  if (loading) return <div>Carregando avaliações...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Avaliações dos Clientes</h1>
      {feedbacks.length === 0 ? (
        <p>Nenhuma avaliação recebida ainda.</p>
      ) : (
        <div className="card-grid">
          {feedbacks.map(fb => (
            <div key={fb.id} className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <h3 className="card-title">Pedido #{fb.orderId}</h3>
                <span className="badge badge-delivered" style={{ fontSize: '1rem' }}>Nota: {fb.rating}/5</span>
              </div>
              <p style={{ marginTop: '0.5rem', color: 'var(--color-text-muted)' }}>
                <small>Por: {fb.userName}</small>
              </p>
              <p className="card-text" style={{ marginTop: '1rem', fontStyle: 'italic' }}>"{fb.comment}"</p>
              <div className="card-footer">
                <small>{new Date(fb.createdAt).toLocaleString('pt-BR')}</small>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Feedbacks;
