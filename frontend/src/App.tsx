import { useState } from 'react';
import type { User } from './types';
import { LoginPage } from './pages/LoginPage';
import { Dashboard } from './pages/Dashboard';
import { Statistics } from './pages/Statistics';
import { Timeline } from './pages/Timeline';

function App() {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [view, setView] = useState<'dashboard' | 'statistics' | 'timeline'>('dashboard');

  if (!currentUser) {
    return <LoginPage onLogin={setCurrentUser} />;
  }

  const handleLogout = () => {
    setCurrentUser(null);
    setView('dashboard');
  };

  return (
    <div>
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 py-2 flex justify-end gap-4">
          <button
            onClick={() => setView('dashboard')}
            className={`px-3 py-1 text-sm font-medium rounded ${view === 'dashboard' ? 'bg-blue-100 text-blue-800' : 'text-gray-600 hover:text-gray-800'}`}
          >
            Dashboard
          </button>
          <button
            onClick={() => setView('statistics')}
            className={`px-3 py-1 text-sm font-medium rounded ${view === 'statistics' ? 'bg-blue-100 text-blue-800' : 'text-gray-600 hover:text-gray-800'}`}
          >
            Statistics
          </button>
          <button
            onClick={() => setView('timeline')}
            className={`px-3 py-1 text-sm font-medium rounded ${view === 'timeline' ? 'bg-blue-100 text-blue-800' : 'text-gray-600 hover:text-gray-800'}`}
          >
            Timeline
          </button>
        </div>
      </div>
      {view === 'dashboard' ? (
        <Dashboard currentUser={currentUser} onLogout={handleLogout} />
      ) : view === 'statistics' ? (
        <Statistics />
      ) : (
        <Timeline />
      )}
    </div>
  );
}

export default App;
