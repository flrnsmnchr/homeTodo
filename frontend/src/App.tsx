import { useState, useEffect } from 'react';
import type { User, Kudo } from './types';
import { LoginPage } from './pages/LoginPage';
import { Dashboard } from './pages/Dashboard';
import { Statistics } from './pages/Statistics';
import { Timeline } from './pages/Timeline';
import { api } from './services/api';

function App() {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [view, setView] = useState<'dashboard' | 'statistics' | 'timeline'>('dashboard');
  const [newKudos, setNewKudos] = useState<Kudo[]>([]);
  const [showKudosModal, setShowKudosModal] = useState(false);

  useEffect(() => {
    if (currentUser) {
      const checkKudos = async () => {
        try {
          const kudos = await api.getUnseenKudos(currentUser.id);
          if (kudos.length > 0) {
            setNewKudos(kudos);
            setShowKudosModal(true);
          }
        } catch (error) {
          console.error('Error checking kudos:', error);
        }
      };
      checkKudos();
    }
  }, [currentUser]);

  const handleCloseKudos = async () => {
    if (currentUser) {
      try {
        await api.markKudosSeen(currentUser.id);
        setShowKudosModal(false);
      } catch (error) {
        console.error('Error marking kudos seen:', error);
      }
    }
  };

  if (!currentUser) {
    return <LoginPage onLogin={setCurrentUser} />;
  }

  const handleLogout = () => {
    setCurrentUser(null);
    setView('dashboard');
  };

  return (
    <div className="min-h-screen bg-gray-100">
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
        <Timeline currentUserId={currentUser.id} />
      )}

      {showKudosModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-6">
            <div className="text-center">
              <div className="text-5xl mb-4">🎉</div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">You got Kudos!</h2>
              <p className="text-gray-600 mb-6">
                Family members appreciated your recent activity:
              </p>
              <div className="space-y-3 mb-8 text-left max-h-48 overflow-y-auto">
                {newKudos.map(kudo => (
                  <div key={kudo.id} className="bg-pink-50 p-3 rounded-lg border border-pink-100">
                    <span className="font-bold text-pink-700">{kudo.giverName}</span>
                    <span className="text-pink-600"> gave you kudos for </span>
                    <span className="font-medium text-pink-700 italic">"{kudo.taskTitle}"</span>
                  </div>
                ))}
              </div>
              <button
                onClick={handleCloseKudos}
                className="w-full py-3 bg-pink-500 text-white rounded-lg font-bold hover:bg-pink-600 transition-colors"
              >
                Awesome!
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
