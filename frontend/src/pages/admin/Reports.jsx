import React, { useState, useEffect } from 'react';
import api from '../../api/client';

const Reports = () => {
  const [topItems, setTopItems] = useState([]);
  const [leastItems, setLeastItems] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const currentMonth = new Date().getMonth() + 1;
  const currentYear = new Date().getFullYear();

  useEffect(() => {
    const fetchReports = async () => {
      try {
        const [topRes, leastRes] = await Promise.all([
          api.get(`/reports/top-items?month=${currentMonth}&year=${currentYear}`),
          api.get(`/reports/least-items?month=${currentMonth}&year=${currentYear}`)
        ]);
        setTopItems(topRes.data);
        setLeastItems(leastRes.data);
      } catch (error) {
        console.error('Failed to load reports', error);
      } finally {
        setLoading(false);
      }
    };
    fetchReports();
  }, [currentMonth, currentYear]);

  if (loading) return <div>Carregando relatórios...</div>;

  return (
    <div>
      <h1 style={{ marginBottom: '2rem' }}>Relatórios ({currentMonth}/{currentYear})</h1>
      
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
        <div className="card">
          <h2 className="card-title">Mais Vendidos</h2>
          <table style={{ width: '100%', textAlign: 'left', marginTop: '1rem', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--color-border)' }}>
                <th style={{ padding: '0.5rem' }}>Item</th>
                <th style={{ padding: '0.5rem' }}>Quantidade</th>
              </tr>
            </thead>
            <tbody>
              {topItems.map((item, idx) => (
                <tr key={idx} style={{ borderBottom: '1px solid var(--color-border)' }}>
                  <td style={{ padding: '0.5rem' }}>{item[0].name}</td>
                  <td style={{ padding: '0.5rem' }}>{item[1]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        <div className="card">
          <h2 className="card-title">Menos Vendidos</h2>
          <table style={{ width: '100%', textAlign: 'left', marginTop: '1rem', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--color-border)' }}>
                <th style={{ padding: '0.5rem' }}>Item</th>
                <th style={{ padding: '0.5rem' }}>Quantidade</th>
              </tr>
            </thead>
            <tbody>
              {leastItems.map((item, idx) => (
                <tr key={idx} style={{ borderBottom: '1px solid var(--color-border)' }}>
                  <td style={{ padding: '0.5rem' }}>{item[0].name}</td>
                  <td style={{ padding: '0.5rem' }}>{item[1]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Reports;
