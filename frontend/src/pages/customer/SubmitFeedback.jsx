import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../../api/client';

const SubmitFeedback = () => {
  const [searchParams] = useSearchParams();
  const orderId = searchParams.get('orderId');
  
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!orderId) {
      setError('ID do pedido não informado.');
      return;
    }

    try {
      await api.post('/feedbacks', {
        orderId: parseInt(orderId),
        rating: parseInt(rating),
        comment
      });
      alert('Avaliação enviada com sucesso!');
      navigate('/order/mine');
    } catch (err) {
      setError('Erro ao enviar avaliação. Você já pode ter avaliado este pedido.');
    }
  };

  return (
    <div style={{ maxWidth: '500px', margin: '0 auto' }}>
      <h1 style={{ marginBottom: '1.5rem' }}>Avaliar Pedido #{orderId}</h1>
      {error && <div style={{ color: 'var(--color-danger)', marginBottom: '1rem', padding: '0.5rem', backgroundColor: 'rgba(239, 68, 68, 0.1)', borderRadius: '4px' }}>{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nota (1 a 5)</label>
          <select className="form-control" value={rating} onChange={(e) => setRating(e.target.value)}>
            <option value="5">5 - Excelente</option>
            <option value="4">4 - Muito Bom</option>
            <option value="3">3 - Bom</option>
            <option value="2">2 - Regular</option>
            <option value="1">1 - Ruim</option>
          </select>
        </div>
        <div className="form-group">
          <label>Comentário</label>
          <textarea 
            className="form-control" 
            rows="5" 
            value={comment} 
            onChange={(e) => setComment(e.target.value)} 
            placeholder="Como foi sua experiência?"
            maxLength="500"
            required
          ></textarea>
        </div>
        <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>Enviar Avaliação</button>
      </form>
    </div>
  );
};

export default SubmitFeedback;
