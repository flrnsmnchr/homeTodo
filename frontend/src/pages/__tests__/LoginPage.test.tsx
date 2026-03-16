import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { LoginPage } from '../LoginPage';
import { api } from '../../services/api';
import type { User, ActivityLog } from '../../types';

// Mock the API service
vi.mock('../../services/api', () => ({
  api: {
    getUsers: vi.fn(),
    getHistory: vi.fn(),
  }
}));

describe('LoginPage', () => {
  const mockUsers: User[] = [
    { id: 1, name: 'User 1' },
    { id: 2, name: 'User 2' }
  ];
  const mockHistory: ActivityLog[] = [
    {
      id: 1,
      taskId: 1,
      action: 'CREATED',
      userId: 1,
      userName: 'User 1',
      timestamp: '2025-01-01T10:00:00Z',
      details: 'Created task 1'
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    (api.getUsers as any).mockResolvedValue(mockUsers);
    (api.getHistory as any).mockResolvedValue(mockHistory);
  });

  it('renders loading state initially', () => {
    (api.getUsers as any).mockReturnValue(new Promise(() => {}));
    (api.getHistory as any).mockReturnValue(new Promise(() => {}));
    const { asFragment } = render(<LoginPage onLogin={vi.fn()} />);
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
    expect(asFragment()).toMatchSnapshot();
  });

  it('renders login page with users and activity (snapshot)', async () => {
    const { asFragment } = render(<LoginPage onLogin={vi.fn()} />);
    
    await waitFor(() => expect(screen.queryByText(/loading/i)).not.toBeInTheDocument());
    
    expect(screen.getAllByText('User 1')[0]).toBeInTheDocument();
    expect(screen.getByText('Recent Activity')).toBeInTheDocument();
    expect(asFragment()).toMatchSnapshot();
  });
});
