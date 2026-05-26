# Copilot Instructions for Acuafit

These rules apply to code generation and pull request reviews.

## Source of truth

- AGENTS.md is the primary source of architecture and domain rules.
- If any skill or local pattern conflicts with AGENTS.md, AGENTS.md wins.

## Domain-critical review priorities

When reviewing changes, prioritize findings in this order:

1. Enrollment and payment integrity.
2. Invitation token security.
3. Billing and invoicing sequencing.
4. Data lifecycle and historical integrity.
5. Architecture and maintainability.

## Non-negotiable business rules

- Enrollment must never be activated before confirmed payment.
- Invitation links must be expiring, single-use, and validated server-side.
- Wompi is the payment source of truth:
  - webhook must be validated
  - transaction status must be verified
  - frontend-only confirmation is not sufficient
- Siigo invoicing happens only after approved and verified payment.
- Students are not identified only by role. Lifecycle history lives in enrollments.
- Enrollment history must not be deleted.

## Frontend architecture guardrails

- Frontend must not implement backend business rules.
- No API calls directly inside components.
- Use service layer plus TanStack Query for server state.
- Zustand is allowed only for UI-only state.
- Avoid introducing global state for server entities.

## Backend architecture guardrails

- Controllers stay thin.
- Validation belongs in request classes.
- Business logic and state transitions belong in services/actions.
- Payment confirmation flows must be idempotent.
- Avoid N+1 and unbounded queries.

## Pull request review style

- Report findings first, ordered by severity.
- Include concrete file references.
- Flag behavior regressions over style issues.
- If no findings, explicitly state no critical issues found and mention residual risks.

## Skills location

- Skills live under .agents/skills.
- Do not reference .github/skills.
