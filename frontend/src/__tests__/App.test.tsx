import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from '../App';
import { api } from '../services/api';

// Mock the API service
vi.mock('../services/api', () => ({
  api: {
    getUsers: vi.fn(),
    getHistory: vi.fn(),
    getTasks: vi.fn(),
  }
}));

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    (api.getUsers as any).mockResolvedValue([{ id: 1, name: 'Test User' }]);
    (api.getHistory as any).mockResolvedValue([]);
    (api.getTasks as any).mockResolvedValue([]);
  });

  it('renders login page initially (snapshot)', async () => {
    const { asFragment } = render(<App />);
    
    await waitFor(() => expect(screen.queryByText(/loading/i)).not.toBeInTheDocument());
    
    expect(screen.getByText('Family Todo App')).toBeInTheDocument();
    expect(screen.getByText('Select User')).toBeInTheDocument();
    expect(asFragment()).toMatchSnapshot();
  });

  it('switches to dashboard on login (snapshot)', async () => {
    render(<App />);
    
    await waitFor(() => expect(screen.queryByText(/loading/i)).not.toBeInTheDocument());
    
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: '1' } });
    
    const loginButton = screen.getByRole('button', { name: /login/i });
    fireEvent.click(loginButton);
    
    await waitFor(() => expect(screen.queryByText(/loading/i)).not.toBeInTheDocument());
    
    expect(screen.getByText(/hello, test user/i)).toBeInTheDocument();
    expect(screen.getByText('Logout')).toBeInTheDocument();
  });
});
