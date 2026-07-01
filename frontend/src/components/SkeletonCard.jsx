import React from 'react';

const SkeletonCard = () => {
  return (
    <div className="card" style={{ pointerEvents: 'none' }}>
      <div className="skeleton skeleton-title"></div>
      <div className="skeleton skeleton-text"></div>
      <div className="skeleton skeleton-text"></div>
      <div className="skeleton skeleton-text short"></div>
      <div className="card-footer" style={{ borderTop: '1px solid var(--color-border)', paddingTop: '1rem' }}>
        <div className="skeleton skeleton-price"></div>
        <div className="skeleton skeleton-btn"></div>
      </div>
    </div>
  );
};

export default SkeletonCard;
