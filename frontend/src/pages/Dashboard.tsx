import { useState, useEffect, useCallback } from 'react';
import type { User, Task, CreateTaskRequest, UpdateTaskRequest } from '../types';
import { api } from '../services/api';
import { TaskList } from '../components/TaskList';
import { TaskForm } from '../components/TaskForm';

type FilterType = 'all' | 'open' | 'completed' | 'my-tasks';
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
  const [editingTask, setEditingTask] = useState<Task | null>(null);
  const [filter, setFilter] = useState<FilterType>('open');
  const [sortBy, setSortBy] = useState<SortType>('createdAt');

  const loadTasks = useCallback(async () => {
    try {
      let tasksData: Task[];
      
      switch (filter) {
        case 'open':
          tasksData = await api.getOpenTasks();
          break;
        case 'completed':
          tasksData = await api.getCompletedTasks();
          break;
        case 'my-tasks':
          tasksData = await api.getMyTasks(currentUser.id);
          break;
        default:
          tasksData = await api.getTasks();
      }
      
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
  }, [filter, sortBy, currentUser.id]);

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

  const handleUpdateTask = async (request: UpdateTaskRequest) => {
    if (!editingTask) return;
    try {
      await api.updateTask(editingTask.id, request, currentUser.id);
      setEditingTask(null);
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

  const handleEdit = (task: Task) => {
    setEditingTask(task);
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
        <div className="mb-6 flex flex-wrap gap-4 items-center justify-between">
          <div className="flex gap-2">
            <button
              onClick={() => setFilter('open')}
              className={`px-4 py-2 rounded ${filter === 'open' ? 'bg-blue-500 text-white' : 'bg-white text-gray-700 border'}`}
            >
              Open
            </button>
            <button
              onClick={() => setFilter('completed')}
              className={`px-4 py-2 rounded ${filter === 'completed' ? 'bg-blue-500 text-white' : 'bg-white text-gray-700 border'}`}
            >
              Completed
            </button>
            <button
              onClick={() => setFilter('my-tasks')}
              className={`px-4 py-2 rounded ${filter === 'my-tasks' ? 'bg-blue-500 text-white' : 'bg-white text-gray-700 border'}`}
            >
              My Tasks
            </button>
            <button
              onClick={() => setFilter('all')}
              className={`px-4 py-2 rounded ${filter === 'all' ? 'bg-blue-500 text-white' : 'bg-white text-gray-700 border'}`}
            >
              All
            </button>
          </div>

          <div className="flex gap-4 items-center">
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as SortType)}
              className="px-3 py-2 border rounded bg-white"
            >
              <option value="createdAt">Sort by Created</option>
              <option value="dueDate">Sort by Due Date</option>
            </select>

            <button
              onClick={() => setShowForm(true)}
              className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
            >
              + New Task
            </button>
          </div>
        </div>

        {/* Task Form */}
        {(showForm || editingTask) && (
          <div className="mb-6">
            <TaskForm
              task={editingTask}
              users={users}
              onSubmit={(request) => {
                if (editingTask) {
                  handleUpdateTask(request as UpdateTaskRequest);
                } else {
                  handleCreateTask(request as CreateTaskRequest);
                }
              }}
              onCancel={() => {
                setShowForm(false);
                setEditingTask(null);
              }}
            />
          </div>
        )}

        {/* Task List */}
        <TaskList
          tasks={tasks}
          currentUserId={currentUser.id}
          onComplete={handleComplete}
          onUncomplete={handleUncomplete}
          onDelete={handleDelete}
          onEdit={handleEdit}
        />
      </main>
    </div>
  );
}
