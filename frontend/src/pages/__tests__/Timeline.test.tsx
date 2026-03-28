import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { Timeline } from '../Timeline';
import { api } from '../../services/api';

// Mock the API service
vi.mock('../../services/api', () => ({
  api: {
    getHistory: vi.fn(),
  }
}));

describe('Timeline', () => {
  const mockHistory = [
    {
      id: 1,
      taskId: 101,
      taskTitle: 'Buy Milk',
      action: 'CREATED',
      userId: 1,
      userName: 'Alice',
      timestamp: '2025-03-28T10:00:00Z',
      details: 'Task created'
    },
    {
      id: 2,
      taskId: 101,
      taskTitle: 'Buy Milk',
      action: 'COMPLETED',
      userId: 2,
      userName: 'Bob',
      timestamp: '2025-03-28T11:00:00Z',
      details: 'Task completed'
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(api.getHistory).mockResolvedValue(mockHistory);
  });

  it('renders loading state initially', () => {
    vi.mocked(api.getHistory).mockReturnValue(new Promise(() => {}));
    render(<Timeline />);
    expect(screen.getByText(/loading timeline.../i)).toBeInTheDocument();
  });

  it('renders timeline correctly after loading', async () => {
    render(<Timeline />);
    
    await waitFor(() => expect(screen.queryByText(/loading timeline.../i)).not.toBeInTheDocument());
    
    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByText('Bob')).toBeInTheDocument();
    expect(screen.getByText(/created/i)).toBeInTheDocument();
    expect(screen.getByText(/completed/i)).toBeInTheDocument();
    expect(screen.getAllByText(/"Buy Milk"/i)).toHaveLength(2);
  });

  it('matches snapshot', async () => {
    const { asFragment } = render(<Timeline />);
    await waitFor(() => expect(screen.queryByText(/loading timeline.../i)).not.toBeInTheDocument());
    expect(asFragment()).toMatchSnapshot();
  });
});
