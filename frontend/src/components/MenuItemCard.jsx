import React from 'react';

const MenuItemCard = ({ item, onAction, actionLabel }) => {
  return (
    <div className="card">
      <h3 className="card-title">{item.name}</h3>
      <p className="card-text">{item.description}</p>
      <div className="card-footer">
        <span className="price">
          {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.price)}
        </span>
        {onAction && actionLabel && (
          <button className="btn btn-primary" onClick={() => onAction(item)}>
            {actionLabel}
          </button>
        )}
      </div>
    </div>
  );
};

export default MenuItemCard;
