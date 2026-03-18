import { useState } from 'react';
import type { Task, User, UpdateTaskRequest } from '../types';
import { TaskItem } from './TaskItem';
import { TaskForm } from './TaskForm';

interface TaskListProps {
  tasks: Task[];
  users: User[];
  currentUserId: number;
  onComplete: (taskId: number) => void;
  onUncomplete: (taskId: number) => void;
  onDelete: (taskId: number) => void;
  onUpdate: (taskId: number, request: UpdateTaskRequest) => Promise<void>;
}

export function TaskList({ tasks, users, currentUserId, onComplete, onUncomplete, onDelete, onUpdate }: TaskListProps) {
  const [editingTaskId, setEditingTaskId] = useState<number | null>(null);

  if (tasks.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500">
        No tasks found
      </div>
    );
  }

  const handleEditSubmit = async (taskId: number, request: UpdateTaskRequest) => {
    await onUpdate(taskId, request);
    setEditingTaskId(null);
  };

  return (
    <div className="space-y-2">
      {tasks.map((task) => (
        editingTaskId === task.id ? (
          <div key={task.id} className="mb-2">
            <TaskForm
              task={task}
              users={users}
              onSubmit={(request) => handleEditSubmit(task.id, request as UpdateTaskRequest)}
              onCancel={() => setEditingTaskId(null)}
            />
          </div>
        ) : (
          <TaskItem
            key={task.id}
            task={task}
            currentUserId={currentUserId}
            onComplete={onComplete}
            onUncomplete={onUncomplete}
            onDelete={onDelete}
            onEdit={() => setEditingTaskId(task.id)}
          />
        )
      ))}
    </div>
  );
}
