import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { Dashboard } from '../Dashboard';
import { api } from '../../services/api';
import type { User, Task } from '../../types';

// Mock the API service
vi.mock('../../services/api', () => ({
  api: {
    getUsers: vi.fn(),
    getOpenTasks: vi.fn(),
    getCompletedTasks: vi.fn(),
    getMyTasks: vi.fn(),
    getTasks: vi.fn(),
    createTask: vi.fn(),
    updateTask: vi.fn(),
    deleteTask: vi.fn(),
    completeTask: vi.fn(),
    uncompleteTask: vi.fn(),
  }
}));

describe('Dashboard', () => {
  const mockUser: User = { id: 1, name: 'Test User' };
  const mockUsers: User[] = [mockUser, { id: 2, name: 'Other User' }];
  const mockTasks: Task[] = [
    {
      id: 1,
      title: 'Task 1',
      description: 'Desc 1',
      status: 'OPEN',
      createdAt: '2025-01-01T10:00:00Z',
      dueDate: null,
      assignedUserId: 1,
      assignedUserName: 'Test User',
      createdByUserId: 1,
      createdByUserName: 'Test User',
      recurrence: null
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    (api.getUsers as any).mockResolvedValue(mockUsers);
    (api.getOpenTasks as any).mockResolvedValue(mockTasks);
  });

  it('renders loading state initially', () => {
    const { asFragment } = render(<Dashboard currentUser={mockUser} onLogout={vi.fn()} />);
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
    expect(asFragment()).toMatchSnapshot();
  });

  it('renders dashboard with tasks after loading (snapshot)', async () => {
    const { asFragment } = render(<Dashboard currentUser={mockUser} onLogout={vi.fn()} />);
    
    await waitFor(() => expect(screen.queryByText(/loading/i)).not.toBeInTheDocument());
    
    expect(screen.getByText('Task 1')).toBeInTheDocument();
    expect(asFragment()).toMatchSnapshot();
  });

  it('renders empty dashboard correctly (snapshot)', async () => {
    (api.getOpenTasks as any).mockResolvedValue([]);
    const { asFragment } = render(<Dashboard currentUser={mockUser} onLogout={vi.fn()} />);
    
    await waitFor(() => expect(screen.queryByText(/loading/i)).not.toBeInTheDocument());
    
    expect(screen.getByText(/no tasks found/i)).toBeInTheDocument();
    expect(asFragment()).toMatchSnapshot();
  });
});
