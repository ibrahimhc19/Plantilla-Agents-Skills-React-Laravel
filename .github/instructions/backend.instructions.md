---
applyTo: "backend/**/*.{php}"
---

Backend-specific instructions for Acuafit.

- Keep controllers thin: request in, service/action call, response out.
- Put validation in FormRequest classes.
- Put state transitions in domain services/actions.
- Preserve enrollment history and student lifecycle integrity.
- Enforce payment-driven enrollment activation.
- Keep payment confirmation idempotent.
- For Wompi: require validated webhook and verified transaction status.
- For Siigo: generate invoices only after payment approval and verification.
- Use eager loading and pagination where appropriate.

Review focus for backend changes:

1. Illegal enrollment/payment state transitions.
2. Missing webhook validation and idempotency guarantees.
3. Business logic inside controllers.
4. Validation bypasses.
5. Query correctness and performance risks.
