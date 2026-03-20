import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import { TaskList } from '../TaskList';
import type { Task } from '../../types';

describe('TaskList', () => {
  const mockTasks: Task[] = [
    {
      id: 1,
      title: 'Task 1',
      description: 'Description 1',
      status: 'OPEN',
      createdAt: '2025-01-01T10:00:00Z',
      dueDate: null,
      assignedUserId: 1,
      assignedUserName: 'User 1',
      createdByUserId: 1,
      createdByUserName: 'User 1',
      recurrence: null
    },
    {
      id: 2,
      title: 'Task 2',
      description: 'Description 2',
      status: 'COMPLETED',
      createdAt: '2025-01-01T10:00:00Z',
      dueDate: null,
      assignedUserId: 1,
      assignedUserName: 'User 1',
      createdByUserId: 1,
      createdByUserName: 'User 1',
      recurrence: 'WEEKLY'
    }
  ];

  const mockProps = {
    tasks: mockTasks,
    users: [{ id: 1, name: 'User 1' }],
    currentUserId: 1,
    onComplete: vi.fn(),
    onUncomplete: vi.fn(),
    onDelete: vi.fn(),
    onUpdate: vi.fn(async () => {})
  };

  it('renders correctly (snapshot)', () => {
    const { asFragment } = render(<TaskList {...mockProps} />);
    expect(asFragment()).toMatchSnapshot();
  });

  it('renders empty list correctly (snapshot)', () => {
    const { asFragment } = render(<TaskList {...mockProps} tasks={[]} />);
    expect(asFragment()).toMatchSnapshot();
  });
});
