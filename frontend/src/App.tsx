import { useState } from 'react';
import { User } from './types';
import { LoginPage } from './pages/LoginPage';
import { Dashboard } from './pages/Dashboard';

function App() {
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  if (!currentUser) {
    return <LoginPage onLogin={setCurrentUser} />;
  }

  return <Dashboard currentUser={currentUser} onLogout={() => setCurrentUser(null)} />;
}

export default App;
