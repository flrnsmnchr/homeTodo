# HomeTodo

HomeTodo is a household task manager for a single family or shared home. It combines a React frontend with a Spring Boot backend and stores data in SQLite. The app is built for lightweight local use: users log in by selecting their name, create and assign chores, track who completed what, and view recent household activity.

## Features

- Username-only login for a fixed household user list
- Create, edit, delete, assign, complete, and reopen tasks
- Optional due dates and recurring tasks (`DAILY`, `WEEKLY`, `MONTHLY`)
- Dashboard filters for task status and assignee
- Household statistics view per user
- Activity timeline with task history
- Kudos system for appreciating completed work
- SQLite persistence with Flyway migrations
- Responsive web UI with tests for backend and frontend code

## Stack

- Backend: Spring Boot 4, Java 26, Gradle
- Frontend: React 19, TypeScript, Vite, Tailwind CSS
- Database: SQLite
- Testing: JUnit Jupiter, Spring test support, Vitest, Testing Library

## Project Structure

```text
.
|-- backend/   Spring Boot API, persistence, packaged web app
|-- frontend/  React/Vite application
|-- SPEC.md    Functional and technical project specification
|-- PROMPT.md  Project prompt/input notes
```

The frontend production build is copied into `backend/src/main/resources/static`, so the backend can serve the complete application as a single deployable JAR.

## Implemented Views

- Login: select a household member and see recent activity
- Dashboard: manage tasks, filter by status, filter to "My Tasks", sort tasks
- Statistics: per-user totals for open and completed tasks
- Timeline: activity feed with kudos interactions

## API Overview

Base path: `/api`

### Users

- `GET /users`
- `GET /users/{id}`
- `GET /users/statistics`

### Tasks

- `GET /tasks`
- `GET /tasks/open`
- `GET /tasks/completed`
- `GET /tasks/my-tasks/{userId}`
- `POST /tasks?createdByUserId={userId}`
- `PUT /tasks/{id}?userId={userId}`
- `DELETE /tasks/{id}`
- `POST /tasks/{id}/complete?userId={userId}`
- `POST /tasks/{id}/uncomplete`

### History

- `GET /history`
- `GET /history/task/{taskId}`

### Kudos

- `POST /kudos/{activityId}?userId={userId}`
- `GET /kudos/unseen?userId={userId}`
- `POST /kudos/mark-seen?userId={userId}`

## Local Development

### Prerequisites

- Java 26
- Node.js and npm

### Backend

```powershell
cd backend
.\gradlew bootRun
```

Backend default URL: `http://localhost:8080`

The backend uses `backend/src/main/resources/application.yml` and creates/uses a local SQLite database file named `hometodo.db`.

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

Frontend default URL: `http://localhost:5173`

The frontend talks to the backend through `/api`. For local development, run both applications at the same time.

## Build

### Frontend Production Build

```powershell
cd frontend
npm run build
```

The `postbuild` step copies the generated frontend files into the backend static resource directory.

### Backend JAR

```powershell
cd backend
.\gradlew clean build
```

After a frontend build has been copied into the backend, the resulting JAR serves both API and UI.

## Tests

### Backend

```powershell
cd backend
.\gradlew test
```

### Frontend

```powershell
cd frontend
npm test -- --run
```

Test suites exist for controllers and services in the backend and for pages/components in the frontend.

### End-to-End

```powershell
cd frontend
npm run test:e2e:install
npm run test:e2e
```

The Playwright suite starts the Spring Boot backend against an isolated SQLite database and runs the Vite frontend with API proxying enabled.

## Seed Data

On startup, the backend seeds default users if the `users` table is empty:

- Florian
- Christiane
- Elisabeth
- Johanna

## Notes

- Authentication is intentionally minimal and not production-ready.
- The repository still contains the default Vite template README at [frontend/README.md](/C:/dev/src/homeTodo/frontend/README.md); the root README is the project-level documentation.
- There is a deployment helper script at [backend/install.ps1](/C:/dev/src/homeTodo/backend/install.ps1) for the original local environment.
