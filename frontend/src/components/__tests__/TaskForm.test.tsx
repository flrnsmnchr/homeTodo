import { describe, it, expect, vi } from 'vitest';
import { render } from '@testing-library/react';
import { TaskForm } from '../TaskForm';
import type { Task, User } from '../../types';

describe('TaskForm', () => {
  const mockUsers: User[] = [
    { id: 1, name: 'User 1' },
    { id: 2, name: 'User 2' }
  ];

  const mockProps = {
    users: mockUsers,
    onSubmit: vi.fn(),
    onCancel: vi.fn()
  };

  it('renders correctly for create (snapshot)', () => {
    const { asFragment } = render(<TaskForm {...mockProps} />);
    expect(asFragment()).toMatchSnapshot();
  });

  it('renders correctly for edit (snapshot)', () => {
    const mockTask: Task = {
      id: 1,
      title: 'Existing Task',
      description: 'Existing Description',
      status: 'OPEN',
      createdAt: '2025-01-01T10:00:00Z',
      dueDate: '2025-01-02T10:00:00Z',
      assignedUserId: 1,
      assignedUserName: 'User 1',
      createdByUserId: 1,
      createdByUserName: 'User 1',
      recurrence: 'DAILY'
    };

    const { asFragment } = render(<TaskForm {...mockProps} task={mockTask} />);
    expect(asFragment()).toMatchSnapshot();
  });
});
