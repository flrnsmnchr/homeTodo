import { expect, test, type Page } from '@playwright/test';


// import { prettyDOM } from '@testing-library/dom';
// import fs from 'node:fs';


async function loginAs(page: Page, userName: string) {
  await page.goto('/');
  await page.getByRole('combobox').selectOption({ label: userName });
  await page.getByRole('button', { name: /login/i }).click();
  await expect(page.getByText(new RegExp(`hello, ${userName}`, 'i'))).toBeVisible();
}

test.describe('HomeTodo app', () => {
  test('logs in and navigates between the main views', async ({ page }) => {
    await loginAs(page, 'Florian');

    await expect(page.getByRole('heading', { name: /family todo app/i })).toBeVisible();

    await page.getByRole('button', { name: 'Statistics' }).click();
    await expect(page.getByRole('heading', { name: /task overview/i })).toBeVisible();
    await expect(page.getByText('Florian')).toBeVisible();

    await page.getByRole('button', { name: 'Timeline' }).click();
    await expect(page.getByRole('heading', { name: /recent activity/i })).toBeVisible();

    await page.getByRole('button', { name: 'Dashboard' }).click();
    await expect(page.getByRole('button', { name: /\+ new task/i })).toBeVisible();
  });

  test('creates and completes a task', async ({ page }) => {
    const taskTitle = `Playwright Task ${Date.now()}`;

    await loginAs(page, 'Florian');

    await page.getByRole('button', { name: /\+ new task/i }).click();
    const form = page.locator('form').last();
    await form.locator('input[type="text"]').fill(taskTitle);
    await form.locator('textarea').fill('Created by Playwright');
    await form.locator('select').first().selectOption({ label: 'Florian' });
    await form.getByRole('button', { name: /create task/i }).click();

    const taskCard = page.locator('div').filter({
      has: page.getByRole('heading', { name: taskTitle }),
    }).first();

    await expect(taskCard).toBeVisible();
    await taskCard.getByRole('button', { name: '✓' }).click();

    await expect(page.getByText(taskTitle)).toHaveCount(0);

    await page.getByRole('button', { name: 'Completed' }).click();
    await expect(page.getByText(taskTitle)).toBeVisible();

    await page.getByRole('button', { name: 'Timeline' }).click();
    await expect(page.locator('div').filter({ hasText: taskTitle }).first()).toBeVisible();
  });

  test('sends kudos between users and shows the kudos modal', async ({ page }) => {
    const taskTitle = `Kudos Task ${Date.now()}`;

    await loginAs(page, 'Florian');

    await page.getByRole('button', { name: /\+ new task/i }).click();
    const form = page.locator('form').last();
    await form.locator('input[type="text"]').fill(taskTitle);
    await form.locator('select').first().selectOption({ label: 'Florian' });
    await form.getByRole('button', { name: /create task/i }).click();

    const taskCard = page.locator('div').filter({
      has: page.getByRole('heading', { name: taskTitle }),
    }).first();
    await taskCard.getByRole('button', { name: '✓' }).click();

    await page.getByRole('button', { name: /logout/i }).click();

    await loginAs(page, 'Johanna');
    await page.getByRole('button', { name: 'Timeline' }).click();

    const activityEntry = page.locator('#timeline-activity-list > div').filter({
      hasText: taskTitle,
      has: page.getByRole('button', { name: /kudos/i }),
    }).first();

    await expect(activityEntry).toBeVisible();
    await activityEntry.getByRole('button', { name: /kudos/i }).click();

    await page.getByRole('button', { name: 'Dashboard' }).click();
    await page.getByRole('button', { name: /logout/i }).click();

    await loginAs(page, 'Florian');
    await expect(page.getByRole('heading', { name: /you got kudos!/i })).toBeVisible();
    await expect(page.getByText('Johanna')).toBeVisible();
    await expect(page.getByText(taskTitle)).toBeVisible();

    // fs.writeFileSync('debug.txt', prettyDOM(document.body, Infinity, { highlight: false }));

    await page.getByRole('button', { name: /awesome!/i }).click();
    await expect(page.getByRole('heading', { name: /you got kudos!/i })).toHaveCount(0);
  });
});
