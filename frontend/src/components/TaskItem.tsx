import type { Task } from '../types';

interface TaskItemProps {
  task: Task;
  currentUserId: number;
  onComplete: (taskId: number) => void;
  onUncomplete: (taskId: number) => void;
  onDelete: (taskId: number) => void;
  onEdit: () => void;
}

export function TaskItem({ task, currentUserId, onComplete, onUncomplete, onDelete, onEdit }: TaskItemProps) {
  const isCompleted = task.status === 'COMPLETED';
  const isAssignedToMe = task.assignedUserId === currentUserId;
  
  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString();
  };

  return (
    <div className={`p-4 border rounded-lg mb-2 ${isCompleted ? 'bg-green-50 border-green-200' : 'bg-white border-gray-200'}`}>
      <div className="flex items-stretch justify-between">
        <div className="flex-1 flex flex-col">
          <div>
            <h3 className={`font-semibold text-lg ${isCompleted ? 'line-through text-gray-500' : 'text-gray-800'}`}>
              {task.title}
            </h3>
            {task.description && (
              <p className="text-gray-600 mt-1">{task.description}</p>
            )}
          </div>
          <div className="flex gap-4 mt-auto pt-2 text-sm text-gray-500">
            {task.assignedUserName && (
              <span className={`px-2 py-0.5 rounded ${isAssignedToMe ? 'bg-blue-100 text-blue-800' : 'bg-gray-100'}`}>
                {task.assignedUserName}
              </span>
            )}
            {task.dueDate && (
              <span>Due: {formatDate(task.dueDate)} <span className="text-gray-400 ml-1">(#{task.id})</span></span>
            )}
            {task.recurrence && (
              <span className="text-purple-600">🔄 {task.recurrence.toLowerCase()}</span>
            )}
          </div>
        </div>
        <div className="flex flex-col gap-2 ml-4">
          {!isCompleted ? (
            <button
              onClick={() => onComplete(task.id)}
              className="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600 text-sm"
            >
              ✓
            </button>
          ) : (
            <button
              onClick={() => onUncomplete(task.id)}
              className="px-3 py-1 bg-yellow-500 text-white rounded hover:bg-yellow-600 text-sm"
            >
              ↩
            </button>
          )}
          <button
            onClick={() => onEdit()}
            className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 text-sm"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(task.id)}
            className="px-3 py-1 bg-red-500 text-white rounded hover:bg-red-600 text-sm"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
}
