import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { Statistics } from '../Statistics';
import { api } from '../../services/api';

// import { prettyDOM } from '@testing-library/dom';
// import fs from 'node:fs';
//fs.writeFileSync('debug.txt', prettyDOM(document.body, Infinity, { highlight: false }));

// Mock the API service
vi.mock('../../services/api', () => ({
  api: {
    getUserStatistics: vi.fn(),
  }
}));

describe('Statistics', () => {
  const mockStats = [
    {
      userId: 1,
      userName: 'Alice',
      totalTasks: 5,
      openTasks: 2,
      completedTasks: 3
    },
    {
      userId: 2,
      userName: 'Bob',
      totalTasks: 3,
      openTasks: 3,
      completedTasks: 0
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(api.getUserStatistics).mockResolvedValue(mockStats);
  });

  it('renders loading state initially', () => {
    vi.mocked(api.getUserStatistics).mockReturnValue(new Promise(() => {}));
    render(<Statistics />);
    expect(screen.getByText(/loading statistics.../i)).toBeInTheDocument();
  });

  it('renders statistics correctly after loading', async () => {
    render(<Statistics />);
    
    await waitFor(() => expect(screen.queryByText(/loading statistics.../i)).not.toBeInTheDocument());

    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByText('Bob')).toBeInTheDocument();
    
    // Check for counts (using getAllByText as some counts might be same)
    expect(screen.getAllByText('5')).toHaveLength(1); // Alice total
    expect(screen.getAllByText('2')).toHaveLength(1); // Alice open
    expect(screen.getAllByText('3')).toHaveLength(3); // Alice done AND Bob total/open
  });

  it('matches snapshot', async () => {
    const { asFragment } = render(<Statistics />);
    await waitFor(() => expect(screen.queryByText(/loading statistics.../i)).not.toBeInTheDocument());
    expect(asFragment()).toMatchSnapshot();
  });
});
