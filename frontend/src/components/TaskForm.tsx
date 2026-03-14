import { useState, useEffect } from 'react';
import { Task, User, CreateTaskRequest, UpdateTaskRequest, RecurrenceType } from '../types';

interface TaskFormProps {
  task?: Task | null;
  users: User[];
  currentUserId: number;
  onSubmit: (request: CreateTaskRequest | UpdateTaskRequest) => void;
  onCancel: () => void;
}

export function TaskForm({ task, users, currentUserId, onSubmit, onCancel }: TaskFormProps) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [assignedUserId, setAssignedUserId] = useState<number | undefined>(undefined);
  const [recurrence, setRecurrence] = useState<RecurrenceType | undefined>(undefined);

  useEffect(() => {
    if (task) {
      setTitle(task.title);
      setDescription(task.description || '');
      setDueDate(task.dueDate ? task.dueDate.slice(0, 16) : '');
      setAssignedUserId(task.assignedUserId || undefined);
      setRecurrence(task.recurrence || undefined);
    } else {
      setTitle('');
      setDescription('');
      setDueDate('');
      setAssignedUserId(undefined);
      setRecurrence(undefined);
    }
  }, [task]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (task) {
      const request: UpdateTaskRequest = {
        title,
        description: description || undefined,
        dueDate: dueDate ? new Date(dueDate).toISOString() : undefined,
        assignedUserId,
        recurrence,
      };
      onSubmit(request);
    } else {
      const request: CreateTaskRequest = {
        title,
        description: description || undefined,
        dueDate: dueDate ? new Date(dueDate).toISOString() : undefined,
        assignedUserId,
        recurrence,
      };
      onSubmit(request);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white p-4 rounded-lg shadow border border-gray-200">
      <div className="grid gap-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={2}
            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Due Date</label>
            <input
              type="datetime-local"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Assign To</label>
            <select
              value={assignedUserId ?? ''}
              onChange={(e) => setAssignedUserId(e.target.value ? Number(e.target.value) : undefined)}
              className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Anyone</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>{user.name}</option>
              ))}
            </select>
          </div>
        </div>
        
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Recurrence</label>
          <select
            value={recurrence ?? ''}
            onChange={(e) => setRecurrence(e.target.value as RecurrenceType | undefined)}
            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">None</option>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
          </select>
        </div>
        
        <div className="flex gap-2 justify-end mt-4">
          <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 border rounded-md hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            {task ? 'Update' : 'Create'} Task
          </button>
        </div>
      </div>
    </form>
  );
}
