import { useState, useEffect, useCallback } from 'react';
import { api } from '../services/api';
import type { UserStatistics } from '../types';

export function Statistics() {
  const [stats, setStats] = useState<UserStatistics[]>([]);
  const [loading, setLoading] = useState(true);

  const loadStats = useCallback(async () => {
    setLoading(true);
    try {
      const data = await api.getUserStatistics();
      setStats(data);
    } catch (error) {
      console.error('Error loading statistics:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadStats();
  }, [loadStats]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p className="text-gray-600">Loading statistics...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8 flex justify-between items-center">
          <h1 className="text-2xl font-bold text-gray-800">Task Overview</h1>
          <button
            onClick={loadStats}
            className="px-3 py-1.5 text-sm bg-white text-gray-700 rounded border hover:bg-gray-50 transition-colors flex items-center gap-2"
            title="Refresh statistics"
          >
            🔄 Reload
          </button>
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          {stats.map((user) => (
            <div key={user.userId} className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
              <h2 className="text-xl font-semibold text-gray-800 mb-4">{user.userName}</h2>
              <div className="grid grid-cols-3 gap-4">
                <div className="text-center">
                  <div className="text-2xl font-bold text-blue-600">{user.totalTasks}</div>
                  <div className="text-xs text-gray-500 uppercase">Total</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-yellow-600">{user.openTasks}</div>
                  <div className="text-xs text-gray-500 uppercase">Open</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-green-600">{user.completedTasks}</div>
                  <div className="text-xs text-gray-500 uppercase">Done</div>
                </div>
              </div>
              <div className="mt-4 w-full bg-gray-200 rounded-full h-2">
                <div
                  className="bg-green-500 h-2 rounded-full"
                  style={{
                    width: `${user.totalTasks > 0 ? (user.completedTasks / user.totalTasks) * 100 : 0}%`,
                  }}
                />
              </div>
              <div className="mt-2 text-right text-xs text-gray-400">
                {user.totalTasks > 0
                  ? Math.round((user.completedTasks / user.totalTasks) * 100)
                  : 0}
                % Completion
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
