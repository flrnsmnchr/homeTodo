import { useState, useEffect, useCallback } from 'react';
import { api } from '../services/api';
import type { ActivityLog } from '../types';

interface TimelineProps {
  currentUserId: number;
}

export function Timeline({ currentUserId }: TimelineProps) {
  const [history, setHistory] = useState<ActivityLog[]>([]);
  const [loading, setLoading] = useState(true);

  const loadHistory = useCallback(async () => {
    setLoading(true);
    try {
      const data = await api.getHistory();
      setHistory(data);
    } catch (error) {
      console.error('Error loading history:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadHistory();
  }, [loadHistory]);

  const handleGiveKudo = async (activityId: number) => {
    try {
      await api.giveKudo(activityId, currentUserId);
      await loadHistory();
    } catch (error) {
      console.error('Error giving kudo:', error);
    }
  };

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
        <div className="mb-8 flex justify-between items-center">
          <h1 className="text-2xl font-bold text-gray-800">Recent Activity</h1>
          <button
            onClick={loadHistory}
            className="px-3 py-1.5 text-sm bg-white text-gray-700 rounded border hover:bg-gray-50 transition-colors flex items-center gap-2"
            title="Refresh activity"
          >
            🔄 Reload
          </button>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="divide-y divide-gray-200">
            {history.length === 0 ? (
              <div className="p-8 text-center text-gray-500">No activity recorded yet.</div>
            ) : (
              history.map((log) => (
                <div key={log.id} className="p-4 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
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
                      
                      {log.userId && log.userId !== currentUserId && (
                        <div className="mt-2 flex items-center gap-2">
                          <button
                            onClick={() => handleGiveKudo(log.id)}
                            className="text-sm px-2 py-1 bg-pink-50 text-pink-600 rounded hover:bg-pink-100 transition-colors border border-pink-100 flex items-center gap-1"
                          >
                            <span>❤️ Kudos</span>
                            {log.kudosCount > 0 && (
                              <span className="font-bold">{log.kudosCount}</span>
                            )}
                          </button>
                        </div>
                      )}
                      {log.userId === currentUserId && log.kudosCount > 0 && (
                        <div className="mt-2 text-xs text-pink-600 font-medium">
                          ❤️ {log.kudosCount} Kudos received
                        </div>
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
