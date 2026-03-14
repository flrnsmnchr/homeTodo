import { useState, useEffect } from 'react';
import type { User, ActivityLog } from '../types';
import { api } from '../services/api';

interface LoginPageProps {
  onLogin: (user: User) => void;
}

export function LoginPage({ onLogin }: LoginPageProps) {
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [recentActivity, setRecentActivity] = useState<ActivityLog[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [usersData, historyData] = await Promise.all([
          api.getUsers(),
          api.getHistory(),
        ]);
        setUsers(usersData);
        setRecentActivity(historyData.slice(0, 5));
      } catch (error) {
        console.error('Error loading data:', error);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  const handleLogin = () => {
    if (selectedUserId) {
      const user = users.find((u) => u.id === selectedUserId);
      if (user) {
        onLogin(user);
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p className="text-gray-600">Loading...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex bg-gray-100">
      {/* Login Section */}
      <div className="w-full md:w-1/2 flex items-center justify-center p-8">
        <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
          <h1 className="text-2xl font-bold text-center mb-6">Family Todo App</h1>
          
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Select User
            </label>
            <select
              value={selectedUserId ?? ''}
              onChange={(e) => setSelectedUserId(e.target.value ? Number(e.target.value) : null)}
              className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">-- Select --</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>{user.name}</option>
              ))}
            </select>
          </div>

          <button
            onClick={handleLogin}
            disabled={!selectedUserId}
            className="w-full py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            Login
          </button>

          <p className="mt-4 text-xs text-gray-500 text-center">
            Note: Username-only login for testing purposes
          </p>
        </div>
      </div>

      {/* Activity Feed Section */}
      <div className="hidden md:w-1/2 bg-blue-500 p-8 flex items-center justify-center">
        <div className="w-full max-w-md">
          <h2 className="text-xl font-semibold text-white mb-4">Recent Activity</h2>
          {recentActivity.length === 0 ? (
            <p className="text-blue-100">No recent activity</p>
          ) : (
            <div className="space-y-3">
              {recentActivity.map((activity) => (
                <div key={activity.id} className="bg-blue-400 p-3 rounded-lg">
                  <p className="text-white text-sm">
                    <span className="font-semibold">{activity.userName || 'System'}</span>
                    {' '}{activity.action.toLowerCase()}
                  </p>
                  {activity.details && (
                    <p className="text-blue-100 text-xs mt-1">{activity.details}</p>
                  )}
                  <p className="text-blue-200 text-xs mt-1">
                    {new Date(activity.timestamp).toLocaleString()}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
