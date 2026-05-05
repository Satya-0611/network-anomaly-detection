import React, { useState, useEffect } from 'react';

export default function App() {
  const [alerts, setAlerts] = useState([]);
  const [isListening, setIsListening] = useState(false);

  useEffect(() => {
    // 1. Open the SSE Connection
    const eventSource = new EventSource('http://localhost:8000/stream');

    eventSource.onopen = () => {
      setIsListening(true);
      console.log('Connected to AI Security Stream');
    };

    // 2. Catch the data pushed by Python
    eventSource.onmessage = (event) => {
      const newAlert = JSON.parse(event.data);
      
      // Add the new alert to the top of our state array
      setAlerts((prevAlerts) => [newAlert, ...prevAlerts]);
    };

    eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      setIsListening(false);
      eventSource.close();
    };

    // 3. The crucial cleanup function you correctly identified
    return () => {
      console.log('Unmounting: Closing SSE Connection');
      eventSource.close();
    };
  }, []);

  return (
    <div style={{ backgroundColor: '#0f172a', color: '#33ff33', minHeight: '100vh', padding: '2rem', fontFamily: 'monospace' }}>
      <header style={{ borderBottom: '1px solid #334155', paddingBottom: '1rem', marginBottom: '2rem' }}>
        <h1 style={{ color: '#f8fafc', margin: 0 }}>Real-Time Anomaly Detection</h1>
        <p style={{ color: isListening ? '#33ff33' : '#ef4444', margin: '1rem 0 0 0' }}>
          Status: {isListening ? '🟢 AI Stream Active' : '🔴 Disconnected'}
        </p>
      </header>

      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
        <h2 style={{ color: '#f8fafc', margin: 0 }}>Live Threat Feed</h2>
        <span style={{ backgroundColor: '#ef4444', color: '#fff', padding: '0.25rem 0.75rem', borderRadius: '9999px', fontWeight: 'bold' }}>
          Total Threats: {alerts.length}
        </span>
      </div>

      <div style={{ backgroundColor: '#1e293b', borderRadius: '0.5rem', padding: '1rem', minHeight: '400px', maxHeight: '600px', overflowY: 'auto', border: '1px solid #334155' }}>
        {alerts.length === 0 ? (
          <p style={{ color: '#94a3b8', textAlign: 'center', marginTop: '2rem' }}>Awaiting initial buffer (200 logs) and threat detection...</p>
        ) : (
          alerts.map((alert, index) => (
            <div key={index} style={{ backgroundColor: '#450a0a', borderLeft: '4px solid #ef4444', padding: '1rem', marginBottom: '0.5rem', borderRadius: '0.25rem', color: '#fca5a5' }}>
              <strong>[🚨 FIRE]</strong> Port <strong>{alert.port}</strong> heavily targeted at IP <strong>{alert.targetIp}</strong>. 
              <span style={{ float: 'right', color: '#f87171' }}>{alert.bytesSent} bytes transferred in {alert.durationMs}ms</span>
            </div>
          ))
        )}
      </div>
    </div>
  );
}