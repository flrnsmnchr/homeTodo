# Family Household Todo App -- Functional Requirements

## User Management

### User Login

-   A user can log in with username no password

### User Profile

A user profile contains: - Name only

------------------------------------------------------------------------

## Task Management

### Create Task

Users can create tasks with: - Title - Description - Category (Cleaning,
Groceries, Laundry, etc.) - Due date (optional) - Assigned user
(optional)

### Edit Task

-   Tasks can be edited after creation.

### Delete Task

-   Tasks can be removed.

### Assign Task

Tasks can be assigned to: - A specific family member - Anyone in the
household

### Task Status

Tasks can have the following statuses: - Open - In Progress - Completed

------------------------------------------------------------------------

## Task Completion

### Mark Task as Done

-   A user can mark a task as completed.

### Completion Metadata

The system records: - Who completed the task - When it was completed

### Undo Completion

-   Completed tasks can optionally be marked as open again.

------------------------------------------------------------------------

## Task History / Activity Tracking

### Completion History

-   Users can view a list of completed tasks.

### Task Activity Log

Each task keeps a history of: - Created by - Edited by - Completed by -
Timestamp of each action

### User Contribution Overview

-   Users can see which tasks each family member completed.

------------------------------------------------------------------------

## Task Overview / Dashboard

### Open Tasks List

-   Shows all open tasks.

### Assigned Tasks View

-   A user can view tasks assigned to them.

### Completed Tasks View

-   Shows tasks already done.
-   It is especially important to see who has done what

### Filtering

Tasks can be filtered by: - Status - Category - Assigned user

### Sorting

Tasks can be sorted by: - Due date - Creation date - Priority

------------------------------------------------------------------------

## Recurring Tasks

### Recurring Tasks

Tasks can repeat: - Daily - Weekly - Monthly

### Automatic Recreation

-   After completion, the system creates the next occurrence.

Examples: - Take out trash (weekly) - Vacuum living room (weekly) - Buy
milk (as needed)

------------------------------------------------------------------------

## Notifications

### Task Notifications

Users receive notifications when: - A task is assigned to them - A task
is due soon

### Completion Notifications

-   Users can see when someone completes a task.

------------------------------------------------------------------------

## Household Statistics (Optional Fun Feature)

### Task Statistics

Statistics include: - Tasks completed per user - Tasks completed this
week

### Leaderboard

-   Shows who completed the most tasks.

------------------------------------------------------------------------

## Data Persistence

### Store Tasks

-   All tasks are stored persistently.

### Store Task History

-   All actions are recorded and stored.

### Household Data Separation

-   Data is separated between households.

------------------------------------------------------------------------

## Multi-Device Access

### Web Access

-   The app can be accessed via a web browser.

### Mobile Support

-   The app is usable on smartphones, Android and Iphone
story

# Tech Stack Specification -- Family Household Todo App

# Backend

## Framework

Spring Boot

Responsibilities: - REST API - Business logic - Persistence - Task
history tracking

## Language

Java 25

## Build System

Gradle with Groovy DSL

Main build file:

    build.gradle

Advantages: - Fast builds - Concise dependency configuration - Excellent
Spring Boot integration

## Persistence Layer

ORM stack: - Spring Data JPA - Hibernate

Responsibilities: - Entity mapping - Repository abstraction - CRUD
operations

## Database

SQLite

Characteristics: - File-based database - Zero configuration -
Lightweight - Ideal for small applications

## Database Migration

Flyway

Purpose: - Version-controlled schema - Reproducible environments -
Easier schema evolution

Migration location:

    src/main/resources/db/migration

## Backend Dependencies

Recommended modules:

-   Spring Web
-   Spring Data JPA
-   SQLite JDBC driver
-   Flyway
-   Spring Boot DevTools

------------------------------------------------------------------------

# Frontend

## Framework

React

Responsibilities: - UI rendering - Interactive task management -
Dashboards - Filtering and sorting

## Language

TypeScript

## Frontend Build Tool

Vite

Reasons: - Extremely fast dev server - Optimized production builds -
Excellent React + TypeScript support

## Styling Framework

Tailwind CSS

------------------------------------------------------------------------

# API Communication

Architecture:

    React (TypeScript + Tailwind)
            |
            | REST / JSON
            |
    Spring Boot (Java 25)
            |
    Spring Data JPA / Hibernate
            |
    SQLite Database

## Example API Endpoints

Tasks:

    GET    /api/tasks
    POST   /api/tasks
    PUT    /api/tasks/{id}
    DELETE /api/tasks/{id}
    POST   /api/tasks/{id}/complete

History:

    GET /api/history

Users:

    GET    /api/users
    POST   /api/users

------------------------------------------------------------------------

# Project Structure

## Backend

    backend/

    src/main/java/net/simnacher/hometodo

        controller/
        service/
        repository/
        model/
        dto/

    src/main/resources
        application.yml
        db/migration

## Frontend

    frontend/

    src/
      components/
      pages/
      services/
      hooks/
      types/

      App.tsx
      main.tsx

------------------------------------------------------------------------

# Data Model

Single household assumption: no Household entity.

Entities:

    User
    Task
    TaskCompletion

Relationships:

    User
      |
      | completes
      |
    TaskCompletion
      |
      | refers to
      |
    Task

## Example Task Fields

    id
    title
    description
    category
    status
    createdAt
    dueDate
    assignedUserId

## TaskCompletion Fields

    id
    taskId
    completedByUserId
    completedAt

------------------------------------------------------------------------

# Development Workflow

Backend:

    ./gradlew bootRun

Runs on:

    localhost:8080

Frontend:

    npm install
    npm run dev

Runs on:

    localhost:5173

------------------------------------------------------------------------

# Version Control

Version control system: Git

Repository hosting: GitHub

------------------------------------------------------------------------

# Final Architecture

    Frontend
    React + TypeScript + Tailwind
            |
            | REST API
            |
    Backend
    Spring Boot (Java 25 + Gradle Groovy)
            |
    Persistence
    Spring Data JPA + Hibernate
            |
    Database
    SQLite
