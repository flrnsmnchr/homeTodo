export type TaskStatus = 'OPEN' | 'COMPLETED';

export type RecurrenceType = 'DAILY' | 'WEEKLY' | 'MONTHLY';

export interface User {
  id: number;
  name: string;
}

export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  createdAt: string;
  dueDate: string | null;
  assignedUserId: number | null;
  assignedUserName: string | null;
  createdByUserId: number;
  createdByUserName: string;
  recurrence: RecurrenceType | null;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  dueDate?: string;
  assignedUserId?: number;
  recurrence?: RecurrenceType;
}

export interface UpdateTaskRequest {
  title?: string;
  description?: string | null;
  status?: TaskStatus;
  dueDate?: string | null;
  assignedUserId?: number | null;
  recurrence?: RecurrenceType | null;
}

export interface UserStatistics {
  userId: number;
  userName: string;
  totalTasks: number;
  openTasks: number;
  completedTasks: number;
}

export interface ActivityLog {
  id: number;
  taskId: number;
  action: string;
  userId: number | null;
  userName: string | null;
  timestamp: string;
  details: string | null;
}
