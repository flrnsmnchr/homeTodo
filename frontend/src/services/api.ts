import { User, Task, CreateTaskRequest, UpdateTaskRequest, ActivityLog, TaskStatus } from '../types';

const API_BASE = 'http://localhost:8080/api';

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });
  
  if (!response.ok) {
    throw new Error(`API Error: ${response.status}`);
  }
  
  return response.json();
}

export const api = {
  // Users
  getUsers: () => request<User[]>(`${API_BASE}/users`),
  getUser: (id: number) => request<User>(`${API_BASE}/users/${id}`),

  // Tasks
  getTasks: (status?: TaskStatus, assignedUserId?: number) => {
    let url = `${API_BASE}/tasks`;
    const params = new URLSearchParams();
    if (status) params.append('status', status);
    if (assignedUserId) params.append('assignedUserId', String(assignedUserId));
    const queryString = params.toString();
    if (queryString) url += `?${queryString}`;
    return request<Task[]>(url);
  },
  
  getOpenTasks: () => request<Task[]>(`${API_BASE}/tasks/open`),
  getCompletedTasks: () => request<Task[]>(`${API_BASE}/tasks/completed`),
  getMyTasks: (userId: number) => request<Task[]>(`${API_BASE}/tasks/my-tasks/${userId}`),
  
  createTask: (request: CreateTaskRequest, createdByUserId: number) =>
    request<Task>(`${API_BASE}/tasks?createdByUserId=${createdByUserId}`, {
      method: 'POST',
      body: JSON.stringify(request),
    }),
  
  updateTask: (id: number, request: UpdateTaskRequest, userId: number) =>
    request<Task>(`${API_BASE}/tasks/${id}?userId=${userId}`, {
      method: 'PUT',
      body: JSON.stringify(request),
    }),
  
  deleteTask: (id: number) =>
    request<void>(`${API_BASE}/tasks/${id}`, { method: 'DELETE' }),
  
  completeTask: (id: number, userId: number) =>
    request<Task>(`${API_BASE}/tasks/${id}/complete?userId=${userId}`, { method: 'POST' }),
  
  uncompleteTask: (id: number) =>
    request<Task>(`${API_BASE}/tasks/${id}/uncomplete`, { method: 'POST' }),

  // History
  getHistory: () => request<ActivityLog[]>(`${API_BASE}/history`),
  getTaskHistory: (taskId: number) => request<ActivityLog[]>(`${API_BASE}/history/task/${taskId}`),
};
