---
name: react-data-fetching-axios
description: Standardize API consumption using Axios, hooks, and proper async state handling. Use when fetching or mutating data.
---

# React Data Fetching Skill

## Goal

Ensure all API interactions are predictable, reusable, and separated from UI.

TanStack Query is the source of truth for server state. Axios provides the transport layer in services.

## Rules

### Architecture

- NEVER call APIs directly inside components
- Use a service layer + custom hooks

Structure:

- /services/userService.ts
- /hooks/useUsers.ts

---

### Service Layer

- All API calls must live in services
- Use Axios instance (preconfigured)

Example:

```ts
import api from "@/lib/api";

export const getUsers = async () => {
  const { data } = await api.get("/users");
  return data;
};
```

---

### Hooks

- Handle loading, error, and data states
- For server state, use TanStack Query hooks that call service functions
- Use useEffect only for UI-side effects, never as the primary server-fetch mechanism

Example:

```ts
import { useQuery } from "@tanstack/react-query";

export const useUsers = () => {
  return useQuery({
    queryKey: ["users"],
    queryFn: getUsers,
  });
};
```

---

### Error Handling

- Never swallow errors
- Always expose error state to UI

---

### UI Usage

- Components only consume hooks

```ts
const { data, loading, error } = useUsers();
```

---

## Anti-patterns

- Fetch inside component body
- Mixing UI and async logic
- Duplicated fetch logic across components
- useEffect + useState for primary server fetching when TanStack Query is available

## Behavior

- Extract API logic into services if missing
- Generate hooks automatically when needed
