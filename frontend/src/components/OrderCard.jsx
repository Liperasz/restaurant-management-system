import React from 'react';

const statusLabels = {
  PENDING: 'Pendente',
  PREPARING: 'Em Preparo',
  DELIVERED: 'Entregue',
  CANCELLED: 'Cancelado'
};

const statusClasses = {
  PENDING: 'badge-pending',
  PREPARING: 'badge-preparing',
  DELIVERED: 'badge-delivered',
  CANCELLED: 'badge-cancelled'
};

const OrderCard = ({ order, onStatusChange, showActions }) => {
  return (
    <div className="card">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
        <h3 className="card-title">Pedido #{order.id}</h3>
        <span className={`badge ${statusClasses[order.status]}`}>
          {statusLabels[order.status]}
        </span>
      </div>
      
      <div className="card-text">
        <p><strong>Data:</strong> {new Date(order.createdAt).toLocaleString('pt-BR')}</p>
        <p><strong>Total:</strong> {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(order.totalPrice)}</p>
        
        <div style={{ marginTop: '1rem' }}>
          <strong>Itens:</strong>
          <ul style={{ listStyleType: 'none', paddingLeft: 0, marginTop: '0.5rem' }}>
            {order.items?.map(item => (
              <li key={item.id} style={{ marginBottom: '0.25rem' }}>
                {item.quantity}x {item.menuItemName || 'Item'} - 
                {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.unitPrice)}
              </li>
            ))}
          </ul>
        </div>
      </div>
      
      {showActions && order.status !== 'DELIVERED' && order.status !== 'CANCELLED' && (
        <div className="card-footer" style={{ gap: '0.5rem', display: 'flex', flexWrap: 'wrap' }}>
          {order.status === 'PENDING' && (
            <button className="btn btn-secondary" onClick={() => onStatusChange(order.id, 'PREPARING')}>
              Preparar
            </button>
          )}
          {order.status === 'PREPARING' && (
            <button className="btn btn-success" style={{ backgroundColor: 'var(--color-success)', color: 'white' }} onClick={() => onStatusChange(order.id, 'DELIVERED')}>
              Entregar
            </button>
          )}
          <button className="btn btn-danger" onClick={() => onStatusChange(order.id, 'CANCELLED')}>
            Cancelar
          </button>
        </div>
      )}
    </div>
  );
};

export default OrderCard;
