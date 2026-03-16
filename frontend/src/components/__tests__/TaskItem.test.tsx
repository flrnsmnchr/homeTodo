import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import { TaskItem } from '../TaskItem';
import type { Task } from '../../types';

describe('TaskItem', () => {
  const mockTask: Task = {
    id: 1,
    title: 'Test Task',
    description: 'Test Description',
    status: 'OPEN',
    createdAt: '2025-01-01T10:00:00Z',
    dueDate: '2025-01-02T10:00:00Z',
    assignedUserId: 1,
    assignedUserName: 'John Doe',
    createdByUserId: 1,
    createdByUserName: 'John Doe',
    recurrence: 'DAILY'
  };

  const mockProps = {
    task: mockTask,
    currentUserId: 1,
    onComplete: vi.fn(),
    onUncomplete: vi.fn(),
    onDelete: vi.fn(),
    onEdit: vi.fn()
  };

  it('renders correctly (snapshot)', () => {
    const { asFragment } = render(<TaskItem {...mockProps} />);
    expect(asFragment()).toMatchSnapshot();
  });

  it('renders completed task correctly (snapshot)', () => {
    const completedTask = { ...mockTask, status: 'COMPLETED' as const };
    const { asFragment } = render(<TaskItem {...mockProps} task={completedTask} />);
    expect(asFragment()).toMatchSnapshot();
  });
});
