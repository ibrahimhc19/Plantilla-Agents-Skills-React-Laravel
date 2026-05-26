---
name: project-structure-enforcement
description: Enforce project folder structure and file placement rules for frontend and backend.
---

# Project Structure Skill

## Goal

Ensure consistent folder organization across the project.

## Base template for directory organization (React + Laravel)

```dir
/frontend
  eslint.config.js
  tsconfig.json
  /src/features
  /src/components
  /src/hooks
  /src/services
  /src/store

/backend
  Laravel structure
```

---

## Frontend Rules

- Components → /components
- Feature-specific logic → /features/{feature}
- API calls → /services
- Hooks → /hooks
- Zustand → /store

---

## Backend Rules

- Controllers → Http/Controllers
- Validation → Http/Requests
- Business logic → Services

---

## Behavior

- Suggest correct file placement
- Reject incorrect structure
- Refactor misplaced files
