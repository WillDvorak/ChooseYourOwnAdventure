# Style Guide

## Frontend (React)

We use functional components with hooks—no class components. React Bootstrap handles most of the UI, and we pass a shared `theme` object down as props to keep colors and styling consistent across components.

Components live in two folders:
- `structural/` for layout components (AppLayout, pages)
- `content/` for reusable UI pieces (Textbox, DisplayBox, etc.)

For naming, we use camelCase for variables and functions, PascalCase for component names. If a function needs explanation, add a JSDoc comment above it with `@param` and `@returns`.

## Backend (Java/Spring Boot)

Standard Spring Boot structure: controllers handle HTTP, services handle business logic, repositories handle data. We use `@Autowired` for dependency injection and `@Transactional` on service classes.

Custom exceptions (`GameSessionNotFoundException`, `InvalidChoiceException`, etc.) are thrown for error cases instead of returning nulls or generic errors.

Public methods in services get a brief JavaDoc comment explaining what they do. Private helper methods don't need them unless the logic is complex.

## General

- Keep functions focused—if it's doing too much, split it up
- Delete code you're not using
- Comments should explain *why*, not *what* (the code shows what)
