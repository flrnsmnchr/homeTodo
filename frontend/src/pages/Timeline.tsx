import { useState, useEffect } from 'react';
import { api } from '../services/api';
import type { ActivityLog } from '../types';

export function Timeline() {
  const [history, setHistory] = useState<ActivityLog[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadHistory() {
      try {
        const data = await api.getHistory();
        setHistory(data);
      } catch (error) {
        console.error('Error loading history:', error);
      } finally {
        setLoading(false);
      }
    }
    loadHistory();
  }, []);

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return date.toLocaleString();
  };

  const getActionColor = (action: string) => {
    switch (action.toUpperCase()) {
      case 'CREATED': return 'text-green-600';
      case 'COMPLETED': return 'text-blue-600';
      case 'UNCOMPLETED': return 'text-yellow-600';
      case 'UPDATED': return 'text-purple-600';
      default: return 'text-gray-600';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p className="text-gray-600">Loading timeline...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Recent Activity</h1>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="divide-y divide-gray-200">
            {history.length === 0 ? (
              <div className="p-8 text-center text-gray-500">No activity recorded yet.</div>
            ) : (
              history.map((log) => (
                <div key={log.id} className="p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-medium text-gray-900">
                        <span className="font-bold">{log.userName || 'System'}</span>
                        {' '}
                        <span className={getActionColor(log.action)}>{log.action.toLowerCase()}</span>
                        {' task: '}
                        <span className="italic">"{log.taskTitle || `Task #${log.taskId}`}"</span>
                      </p>
                      {log.details && (
                        <p className="text-sm text-gray-600 mt-1">{log.details}</p>
                      )}
                    </div>
                    <span className="text-xs text-gray-400 whitespace-nowrap ml-4">
                      {formatDate(log.timestamp)}
                    </span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
