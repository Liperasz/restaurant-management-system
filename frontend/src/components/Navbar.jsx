import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">Camarada Camarão</Link>
      </div>
      <div className="navbar-links">
        <Link to="/menu">Cardápio</Link>
        {user?.role === 'CUSTOMER' && (
          <>
            <Link to="/order/new">Fazer Pedido</Link>
            <Link to="/order/mine">Meus Pedidos</Link>
          </>
        )}
        {(user?.role === 'ATTENDANT' || user?.role === 'ADMINISTRATOR') && (
          <Link to="/attendant/orders">Pedidos Ativos</Link>
        )}
        {user?.role === 'ADMINISTRATOR' && (
          <>
            <Link to="/admin/menu">Gerenciar Cardápio</Link>
            <Link to="/admin/stock">Estoque</Link>
            <Link to="/admin/staff">Equipe</Link>
            <Link to="/admin/reports">Relatórios</Link>
            <Link to="/admin/history">Histórico</Link>
            <Link to="/admin/feedbacks">Avaliações</Link>
          </>
        )}
      </div>
      <div className="navbar-auth">
        {user ? (
          <button onClick={handleLogout} className="btn btn-outline">Sair</button>
        ) : (
          <>
            <Link to="/login" className="btn btn-primary">Entrar</Link>
            <Link to="/register" className="btn btn-secondary">Cadastrar</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
