# AGENTS.md — Acuafit

## Project Overview

Acuafit is a swimming academy management platform focused on:

- Student onboarding
- Enrollment workflows
- Payment automation
- Electronic invoicing
- Attendance tracking
- Administrative management

Architecture:

- Frontend: React + TypeScript + Vite
- Backend: Laravel API
- Database: PostgreSQL
- Auth: Laravel Sanctum
- Data fetching: TanStack Query

---

# Core Architecture Principles

## Backend is the source of truth

Never duplicate backend business logic in frontend code.

Frontend responsibilities:
- rendering
- forms
- UI state
- optimistic UX only

Backend responsibilities:
- validation
- permissions
- business rules
- state transitions
- payment verification

---

# Business Domain Rules

## Students are NOT represented only by roles

A student may:
- enroll multiple times
- change levels/groups
- pause studies
- rejoin later

Student history MUST be preserved.

Use enrollments for academic lifecycle tracking.

---

## Payments drive enrollment activation

Enrollment flow:

```text
Invitation
→ PreEnrollment
→ Pending Payment
→ Payment Approved
→ Active Enrollment
```

Never activate enrollments before payment confirmation.

---

## Invitation tokens

Invitation links:

* must expire
* must be single-use
* must be validated server-side

Tokens are sent manually through WhatsApp.

---

## Responsible adults / parents

Minors must be linked to a responsible adult.

A responsible adult may:

* manage multiple students
* pay for multiple students
* view payments and attendance

Do not duplicate billing data unnecessarily.

---

## Wompi is payment source of truth

Successful payments must come from:

* validated webhook
* verified transaction status

Never trust frontend-only payment responses.

---

## Siigo integration

Electronic invoicing happens ONLY after:

* payment approval
* successful Wompi verification

---

# Core Entities

## User

Authentication entity.

Roles:

* super_admin
* administrativo
* profesor
* contador

Do NOT use role alone to identify students.

---

## Student

Personal student data only.

Contains:

* name
* document
* birth date
* contact data

No enrollment lifecycle logic here.

---

## ResponsibleAdult

Represents:

* parents
* guardians
* spouses
* billing contacts

Supports multiple linked students.

---

## Enrollment

Represents an academic enrollment.

Contains:

* group
* level
* status
* dates
* billing relationship

Enrollment history must never be deleted.

---

## Invitation

Temporary onboarding invitation.

Contains:

* token
* expiration
* selected group
* selected venue

---

## Payment

Represents money received.

Types:

* matricula
* mensualidad
* trimestre
* paquete
* clase_individual

---

## Charge / BillingItem

Represents an amount owed.

Separate from payments.

---

## Invoice

Represents fiscal/electronic invoice.

Generated after confirmed payment.

---

## Group

Academic group.

Relations:

* venue
* level
* teacher

---

## Venue

Physical location / sede.

---

# Enrollment States

Allowed states:

```text
pending_payment
payment_under_review
approved
active
paused
finished
rejected
withdrawn
```

State transitions must happen in backend services/actions.

---

# Payment States

Allowed states:

```text
pending
under_review
paid
expired
partial
rejected
```

---

# Frontend Architecture

## State Management

Use:

* TanStack Query for server state
* local state for UI state
* Context when appropriate

Avoid global stores unless strictly necessary.

Zustand is allowed ONLY for:

* UI-only state
* temporary wizard state
* sidebar/layout state

Do NOT use Zustand for:

* API cache
* students
* payments
* enrollments
* authentication data

---

## Data Fetching

Use TanStack Query for:

* caching
* invalidation
* pagination
* optimistic updates

Query keys must be structured:

```ts
['students']
['students', filters]
['payments', studentId]
['groups', venueId]
```

---

## Forms

Use:

* React Hook Form
* Zod validation

Requirements:

* reusable form components
* schema-driven validation
* minimal duplicated logic

---

## Component Architecture

Prefer:

* small reusable components
* composition
* feature-based organization

Avoid:

* giant pages
* deeply nested prop drilling
* business logic inside UI components

---

# UI / Design System Rules

## Design Tokens

Never hardcode colors in React components.

Use semantic tokens only.

Examples:

* primary
* secondary
* accent
* destructive
* muted

---

## Reusable UI Primitives

Reuse patterns for:

* tables
* headers
* forms
* cards
* modals

Avoid duplicated Tailwind class blocks.

---

## Tables

Admin tables must support:

* pagination
* loading states
* empty states
* filters
* responsive overflow

---

## UX Rules

Prioritize:

* operational clarity
* low friction
* fast workflows

This is an operations-heavy SaaS.

Avoid overdesigned UI.

---

# Laravel Backend Rules

## Controllers

Controllers must stay thin.

Controllers should:

* validate request
* call action/service
* return response

Never place business logic inside controllers.

---

## Business Logic

Use:

* Actions
* Services
* Domain-oriented classes

Examples:

* CreateInvitationAction
* ApprovePaymentAction
* GenerateInvoiceAction

---

## Database

Use:

* eager loading
* indexes
* transactions where appropriate

Avoid:

* N+1 queries
* fat models
* duplicated state

---

## Validation

Always validate:

* invitation tokens
* enrollment transitions
* payment ownership
* webhook authenticity

---

## Idempotency

Payment confirmation flows MUST be idempotent.

Webhook retries must not duplicate:

* invoices
* enrollments
* payments

---

# Authentication

Current auth:

* Sanctum

Future auth:

* Google OAuth via Socialite

Social login should connect to existing users when possible.

---

# Attendance Module

Attendance MVP is intentionally simple.

Teachers should only:

* open group
* mark attendance
* submit

Avoid overengineering attendance logic.

---

# Notifications

Initial communication channel:

* manual WhatsApp

Email notifications are secondary.

Do not assume users read emails consistently.

---

# PDF Rules

PDF rendering requirements:

* use table layouts
* avoid flex/grid
* use DejaVu Sans
* avoid unsupported CSS
* keep layouts print-safe

Never reuse complex web layouts inside PDFs.

---

# Refactor Guidelines

This project is a refactor/evolution of an existing system.

Before rewriting:

* evaluate reuse potential
* preserve working CRUD modules when reasonable

Safe to reuse partially:

* venues
* groups

Needs significant refactor:

* students
* payments
* invoices

---

# Code Quality Rules

Prioritize:

* maintainability
* readability
* business clarity

Avoid:

* premature abstractions
* unnecessary patterns
* overengineering

Prefer explicit business code over "clever" code.

---

# Current MVP Priorities

Highest priority modules:

1. Invitations
2. PreEnrollment
3. Payments
4. Wompi
5. Siigo
6. Student panel
7. Admin panel
8. Attendance

Everything else is secondary.
