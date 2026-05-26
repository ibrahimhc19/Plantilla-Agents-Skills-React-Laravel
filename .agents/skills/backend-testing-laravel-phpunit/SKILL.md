---
name: backend-testing-laravel-phpunit
description: Enforce high-quality backend tests for Acuafit Laravel APIs using PHPUnit. Covers HTTP feature tests, service/action tests, form request validation, factories, database assertions, and external integration fakes. Tests are isolated, deterministic, and aligned with AGENTS.md domain rules.
---

# Backend Testing - Laravel + PHPUnit (Acuafit)

Stack: Laravel 12, PHPUnit, Eloquent Factories, RefreshDatabase, Laravel Fakes  
Scope: feature tests (HTTP/API), service/action tests, form requests, model relations, jobs/events, external integrations

---

## Repository Baseline

Current backend baseline in this repo:

- `backend/phpunit.xml` uses:
  - `DB_CONNECTION=sqlite`
  - `DB_DATABASE=:memory:`
  - `CACHE_STORE=array`
  - `QUEUE_CONNECTION=sync`
  - `MAIL_MAILER=array`
  - `SESSION_DRIVER=array`
- Test suites:
  - `tests/Feature`
  - `tests/Unit`
- Base app test class:
  - `backend/tests/TestCase.php` extends Laravel `BaseTestCase`

Use this baseline as default. Do not propose alternative config unless explicitly needed.

---

## Domain-Critical Test Priorities

From AGENTS.md, test these first:

1. Enrollment is never activated before confirmed payment.
2. Invitation tokens are expiring, single-use, and server-validated.
3. Wompi is the payment source of truth (validated webhook + verified status).
4. Siigo invoicing only after approved and verified payment.
5. Enrollment history is preserved (no destructive lifecycle regression).

When scope is large, prioritize these scenarios over generic CRUD coverage.

---

## Test Class Rules

### Feature tests

- Namespace: `Tests\\Feature\\...`
- Extend `Tests\\TestCase`
- Use `RefreshDatabase` when DB is touched
- Use JSON assertions for APIs (`getJson`, `postJson`, etc.)

### Unit/service/action tests

- If using container, models, config, facades, or DB: extend `Tests\\TestCase`
- Use plain `PHPUnit\\Framework\\TestCase` only for pure, framework-independent logic

This avoids broken tests for classes that require Laravel bootstrapping.

---

## Naming and Structure for Acuafit

Use repository domain names in tests and examples:

- Models: `Estudiante`, `Pago`, `Factura`, `Acudiente`, `Grupo`, `Sede`, `User`
- Tables: `estudiantes`, `pagos`, `facturas`, etc.

Preferred test layout:

```text
tests/
  Feature/
    Estudiantes/
    Pagos/
    Facturas/
    Invitations/
  Unit/
    Services/
    Actions/
    Requests/
    Models/
```

Feature tests should mirror business capabilities, not raw controller names.

---

## Factories in This Repo

Only `UserFactory` exists by default. If tests need more domain entities:

- add dedicated factories (`EstudianteFactory`, `PagoFactory`, `FacturaFactory`, etc.)
- prefer realistic defaults and state methods
- never replace factories with raw `DB::insert` setup

Use `Model::factory()` consistently for setup.

---

## Feature Test Guidance (HTTP)

For each protected endpoint, test at least:

- success path (200/201/204)
- validation failure (422)
- auth/authorization behavior (401/403) when applicable
- not found (404) when resource ID is invalid
- side effects in DB (`assertDatabaseHas`, `assertDatabaseMissing`)

Example style:

```php
<?php

namespace Tests\Feature\Pagos;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ListPagosTest extends TestCase
{
    use RefreshDatabase;

    public function test_authenticated_user_can_list_pagos(): void
    {
        $user = User::factory()->create();

        $response = $this->actingAs($user)
            ->getJson('/api/pagos');

        $response->assertOk();
    }

    public function test_guest_request_is_rejected_when_route_is_protected(): void
    {
        $response = $this->getJson('/api/pagos');

        $response->assertStatus(401);
    }
}
```

Note: If route is intentionally public, assert expected public behavior instead.

---

## Form Request Validation Tests

When request rules are non-trivial, test rule branches directly via Validator:

- required fields
- type constraints
- allowed value sets
- edge boundaries

Keep request tests in `tests/Unit/Requests` or `tests/Unit/Schemas`.

---

## Service and Action Tests

Test service/action outcomes, not internals:

- return values/DTOs
- thrown domain exceptions
- DB state transitions
- dispatched jobs/events (using fakes)

Mock only external boundaries (gateway clients, storage, HTTP providers).

Avoid mocking Eloquent models directly.

---

## Integration Boundaries and Fakes

Preferred Laravel fakes:

- `Http::fake()` for Wompi/Siigo/external APIs
- `Queue::fake()` for queued jobs
- `Mail::fake()` for mail side effects
- `Notification::fake()` for notifications
- `Event::fake()` for event dispatch assertions
- `Storage::fake()` for upload/support files

Example:

```php
Http::fake([
    'api.wompi.co/*' => Http::response(['status' => 'APPROVED'], 200),
]);
```

Never call real external services in CI tests.

---

## Database Assertions

Use structured DB assertions after behavior:

```php
$this->assertDatabaseHas('estudiantes', [
    'documento_identidad' => '10203040',
]);

$this->assertDatabaseMissing('pagos', ['id' => $pagoId]);
$this->assertDatabaseCount('facturas', 1);
```

If model is soft-deletable, prefer `assertSoftDeleted` over hard-delete assumptions.

---

## API Response Assertions

For JSON APIs, prefer:

- `assertJsonStructure`
- `assertJsonPath`
- `assertJsonFragment`
- `assertJsonCount`

Avoid string-level response assertions for API payload validation.

---

## Time and Async Rules

- Use Laravel time helpers (`$this->travel()`) for expiration and deadline logic.
- Do not use `sleep()` in tests.
- Test queued behavior with `Queue::fake()` + dispatch assertions.
- Test job/listener logic in isolation in dedicated unit tests.

---

## Authorization Tests

For protected operations, include both allowed and denied paths.

If using roles/permissions (Spatie), create users with explicit role state in test setup and assert:

- authorized user succeeds
- authenticated but unauthorized user is forbidden
- guest is unauthorized (when route requires auth)

---

## Anti-Patterns to Avoid

- raw `DB::insert` test setup when factory can be used
- giant `setUp()` with unnecessary fixtures
- status-code-only assertions without side effect checks
- testing private methods directly
- mocking the class under test
- brittle assertions tightly coupled to framework internals

---

## Coverage Guidance

Aim for meaningful, risk-based coverage rather than vanity percentage.

Minimum recommended progression:

- Phase 1: critical payment/enrollment/invitation flows covered
- Phase 2: major API endpoints covered (happy + validation + auth)
- Phase 3: service/action branch coverage for domain logic

Use coverage to discover gaps, not as the only quality signal.

---

## Shipping Checklist

- [ ] Test class scope matches location (`Feature` vs `Unit`)
- [ ] DB-changing tests use `RefreshDatabase`
- [ ] Domain names match repo (`Estudiante`, `Pago`, `Factura`, etc.)
- [ ] Model setup uses factories (or explicitly documented exception)
- [ ] Validation failures assert 422 + exact validation keys
- [ ] Auth/authorization scenarios are tested where applicable
- [ ] External integrations are faked (`Http`, `Queue`, `Mail`, etc.)
- [ ] API responses are asserted structurally (`assertJson*`)
- [ ] Payment/enrollment/invitation critical rules are covered
- [ ] No `sleep()` or environment-coupled assumptions
