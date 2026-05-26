---
name: react-tanstack-query
description: Enforce best practices for server-state management using TanStack Query with Axios. Use when fetching, caching, or mutating server data.
---

# TanStack Query Skill

## Goal

Manage server state efficiently using TanStack Query with caching, synchronization, and proper separation of concerns.

---

## Core Principles

- TanStack Query manages ALL server state
- No manual useEffect + useState for fetching
- API calls still live in service layer (Axios)

---

## Architecture

Structure:

- /services/userService.ts
- /hooks/queries/useUsers.ts
- /hooks/mutations/useCreateUser.ts

---

## Service Layer (MANDATORY)

- All API calls must be defined in services

Example:

```ts
import api from "@/lib/api";

export const getUsers = async () => {
  const { data } = await api.get("/users");
  return data;
};
```

---

## Queries

- Always use `useQuery`
- Query keys must be structured and predictable

Example:

```ts
import { useQuery } from "@tanstack/react-query";
import { getUsers } from "@/services/userService";

export const useUsers = () => {
  return useQuery({
    queryKey: ["users"],
    queryFn: getUsers,
  });
};
```

---

## Mutations

- Use `useMutation` for create/update/delete
- Invalidate queries after mutation

Example:

```ts
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createUser } from "@/services/userService";

export const useCreateUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] });
    },
  });
};
```

---

## Query Keys (IMPORTANT)

- Must be consistent and hierarchical

Examples:

- ["users"]
- ["users", id]
- ["users", { page: 1 }]

---

## UI Usage

- Components consume hooks only

```ts
const { data, isLoading, error } = useUsers();
```

---

## Error Handling

- Always expose error from query/mutation
- Handle UI states (loading, error, empty)

---

## Caching Rules

- Do NOT refetch unnecessarily
- Configure staleTime when appropriate

Example:

```ts
useQuery({
  queryKey: ["users"],
  queryFn: getUsers,
  staleTime: 1000 * 60 * 5,
});
```

---

## Anti-patterns

- useEffect + fetch when TanStack Query is available
- API calls inside components
- Not invalidating queries after mutations
- Random/unstructured query keys

---

## Behavior

- Replace manual fetching with TanStack Query
- Extract logic into query/mutation hooks
- Suggest cache invalidation strategies
- Enforce consistent query key structure
