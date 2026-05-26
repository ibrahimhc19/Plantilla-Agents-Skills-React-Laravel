# Summary

Describe what this PR does.

---

## Business Impact (MANDATORY)

- [ ] This change does not activate enrollments before payment confirmation
- [ ] Invitation token rules are preserved (expiration, single-use, server-side validation)
- [ ] Wompi remains the payment source of truth (validated webhook + verified transaction status)
- [ ] Siigo invoicing remains after approved/verified payment only
- [ ] Student lifecycle history is preserved through enrollments (no role-only modeling)

---

## Architecture Checklist (MANDATORY)

- [ ] No API calls inside components
- [ ] Business logic is not in frontend
- [ ] TanStack Query used for server state
- [ ] Zustand used only for client state
- [ ] Query keys are structured correctly
- [ ] No hardcoded strings or magic numbers
- [ ] Constants/types/utils are properly extracted
- [ ] Code follows feature-based structure

---

## Testing Evidence (MANDATORY)

- [ ] Backend tests executed (php artisan test)
- [ ] Frontend quality checks executed (npm run lint && npm run build)
- [ ] If payments/enrollments changed, transition scenarios were validated

Evidence:

- Backend:
- Frontend:
- Domain scenarios:

---

## Skills Applied

Mention relevant skills:

- react-tanstack-query
- frontend-code-organization
- laravel-api-architecture

---

## Notes for Review

- Follow AGENTS.md strictly
- Apply rules from `.agents/skills`
- Suggest refactors if architecture is violated
- Do not repeat unresolved comments

---

## Risk and Rollback

Risk level: Low / Medium / High

Rollback plan:
