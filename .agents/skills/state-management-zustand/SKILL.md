---
name: state-management-zustand
description: Use Zustand only for lightweight UI/global client state. Avoid using it for server state or async data management.
---

# Zustand State Management Rules

## Goal

Use Zustand sparingly and only for UI-oriented global state.

Server state must be handled with TanStack Query.

---

# When to Use Zustand

Use Zustand ONLY for:

- Sidebar/layout state
- Modal visibility
- Wizard/multi-step temporary state
- UI preferences
- Non-persistent client-only state

---

# When NOT to Use Zustand

Do NOT use Zustand for:

- API cache
- Payments
- Students
- Enrollments
- Authentication server state
- Async data fetching
- Business logic

Use:

- TanStack Query for server state
- useState for local component state
- Context when appropriate

---

# Store Structure

- One small store per concern
- Keep stores minimal
- Avoid giant global stores

Example:

```ts
import { create } from "zustand";

interface UiState {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
}

export const useUiStore = create<UiState>((set) => ({
  sidebarOpen: false,
  setSidebarOpen: (open) => set({ sidebarOpen: open }),
}));