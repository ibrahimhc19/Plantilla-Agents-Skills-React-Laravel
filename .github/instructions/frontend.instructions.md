---
applyTo: "frontend/src/**/*.{ts,tsx,css}"
---

Frontend-specific instructions for Acuafit.

- Backend is the source of truth for validations, permissions, business rules, and state transitions.
- Never duplicate payment, enrollment, or invitation business logic in frontend.
- Use TanStack Query for server state.
- Keep Axios API calls in services; UI consumes hooks only.
- Do not use Zustand for server entities (students, payments, enrollments, auth data).
- Use React Hook Form + Zod for forms.
- Prefer semantic design tokens and shared UI primitives.
- Keep components focused and avoid mixing UI rendering with orchestration logic.

Review focus for frontend changes:

1. Domain leakage into UI.
2. Data fetching architecture regressions.
3. Query key consistency.
4. Form validation coverage.
5. State management misuse.
