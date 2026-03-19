import { useState, useEffect, useCallback } from 'react';
import type { User, Task, CreateTaskRequest, UpdateTaskRequest } from '../types';
import { api } from '../services/api';
import { TaskList } from '../components/TaskList';
import { TaskForm } from '../components/TaskForm';

type StatusFilter = 'ALL' | 'OPEN' | 'COMPLETED';
type AssigneeFilter = 'ALL' | 'MY_TASKS';
type SortType = 'createdAt' | 'dueDate';

interface DashboardProps {
  currentUser: User;
  onLogout: () => void;
}

export function Dashboard({ currentUser, onLogout }: DashboardProps) {
  const [users, setUsers] = useState<User[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('OPEN');
  const [assigneeFilter, setAssigneeFilter] = useState<AssigneeFilter>('ALL');
  const [sortBy, setSortBy] = useState<SortType>('dueDate');

  const loadTasks = useCallback(async () => {
    try {
      const status = statusFilter === 'ALL' ? undefined : statusFilter as Task['status'];
      const assignedUserId = assigneeFilter === 'MY_TASKS' ? currentUser.id : undefined;
      
      const tasksData = await api.getTasks(status, assignedUserId);
      
      // Sort tasks
      tasksData.sort((a, b) => {
        if (sortBy === 'dueDate') {
          if (!a.dueDate && !b.dueDate) return 0;
          if (!a.dueDate) return 1;
          if (!b.dueDate) return -1;
          return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
        }
        return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      });
      
      setTasks(tasksData);
    } catch (error) {
      console.error('Error loading tasks:', error);
    }
  }, [statusFilter, assigneeFilter, sortBy, currentUser.id]);

  useEffect(() => {
    async function loadData() {
      try {
        const [usersData] = await Promise.all([
          api.getUsers(),
        ]);
        setUsers(usersData);
        await loadTasks();
      } catch (error) {
        console.error('Error loading data:', error);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [loadTasks]);

  const handleCreateTask = async (request: CreateTaskRequest) => {
    try {
      await api.createTask(request, currentUser.id);
      setShowForm(false);
      await loadTasks();
    } catch (error) {
      console.error('Error creating task:', error);
    }
  };

  const handleUpdateTask = async (taskId: number, request: UpdateTaskRequest) => {
    try {
      await api.updateTask(taskId, request, currentUser.id);
      await loadTasks();
    } catch (error) {
      console.error('Error updating task:', error);
    }
  };

  const handleComplete = async (taskId: number) => {
    try {
      await api.completeTask(taskId, currentUser.id);
      await loadTasks();
    } catch (error) {
      console.error('Error completing task:', error);
    }
  };

  const handleUncomplete = async (taskId: number) => {
    try {
      await api.uncompleteTask(taskId);
      await loadTasks();
    } catch (error) {
      console.error('Error uncompleting task:', error);
    }
  };

  const handleDelete = async (taskId: number) => {
    if (!confirm('Are you sure you want to delete this task?')) return;
    try {
      await api.deleteTask(taskId);
      await loadTasks();
    } catch (error) {
      console.error('Error deleting task:', error);
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
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-xl font-bold text-gray-800">Family Todo App</h1>
          <div className="flex items-center gap-4">
            <span className="text-gray-600">Hello, {currentUser.name}</span>
            <button
              onClick={onLogout}
              className="px-3 py-1 text-sm text-gray-600 hover:text-gray-800"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-6">
        {/* Controls */}
        <div className="mb-6 flex flex-wrap gap-6 items-center justify-between">
          <div className="flex flex-wrap gap-6">
            <div className="flex flex-col gap-2">
              <span className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Status</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setStatusFilter('OPEN')}
                  className={`px-3 py-1.5 text-sm rounded transition-colors ${statusFilter === 'OPEN' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border hover:bg-gray-50'}`}
                >
                  Open
                </button>
                <button
                  onClick={() => setStatusFilter('COMPLETED')}
                  className={`px-3 py-1.5 text-sm rounded transition-colors ${statusFilter === 'COMPLETED' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border hover:bg-gray-50'}`}
                >
                  Completed
                </button>                <button
                  onClick={() => setStatusFilter('ALL')}
                  className={`px-3 py-1.5 text-sm rounded transition-colors ${statusFilter === 'ALL' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border hover:bg-gray-50'}`}
                >
                  All
                </button>
              </div>
            </div>

            <div className="flex flex-col gap-2">
              <span className="text-sm font-semibold text-gray-500 uppercase tracking-wider">Assignee</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setAssigneeFilter('MY_TASKS')}
                  className={`px-3 py-1.5 text-sm rounded transition-colors ${assigneeFilter === 'MY_TASKS' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border hover:bg-gray-50'}`}
                >
                  My Tasks
                </button>
                <button
                  onClick={() => setAssigneeFilter('ALL')}
                  className={`px-3 py-1.5 text-sm rounded transition-colors ${assigneeFilter === 'ALL' ? 'bg-blue-600 text-white' : 'bg-white text-gray-700 border hover:bg-gray-50'}`}
                >
                  All Tasks
                </button>
              </div>
            </div>
          </div>

          <div className="flex gap-4 items-center">
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as SortType)}
              className="px-3 py-2 border rounded bg-white text-sm"
            >
              <option value="createdAt">Sort by Created</option>
              <option value="dueDate">Sort by Due Date</option>
            </select>

            <button
              onClick={() => setShowForm(true)}
              className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 text-sm font-medium"
            >
              + New Task
            </button>
          </div>
        </div>

        {/* Create Task Form */}
        {showForm && (
          <div className="mb-6">
            <TaskForm
              users={users}
              onSubmit={(request) => handleCreateTask(request as CreateTaskRequest)}
              onCancel={() => setShowForm(false)}
            />
          </div>
        )}

        {/* Task List */}
        <TaskList
          tasks={tasks}
          users={users}
          currentUserId={currentUser.id}
          onComplete={handleComplete}
          onUncomplete={handleUncomplete}
          onDelete={handleDelete}
          onUpdate={handleUpdateTask}
        />
      </main>
    </div>
  );
}
