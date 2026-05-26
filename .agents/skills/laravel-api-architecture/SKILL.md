---
name: laravel-api-architecture
description: Enforce clean backend architecture in Laravel APIs with proper separation of concerns, validation, and database interaction. Use when creating or refactoring backend features.
---

# Laravel API Architecture Skill

## Goal

Build scalable, maintainable APIs using Laravel with clear separation between controllers, services, and data access.

---

## Core Principles

- Controllers are thin
- Business logic lives in services
- Validation is handled via Form Requests
- Database logic is controlled and optimized

---

## Controller Rules

- Controllers must NOT contain business logic
- Only responsibilities:
  - Receive request
  - Call service
  - Return response

Example:

```php
public function store(StoreUserRequest $request)
{
    $user = $this->userService->create($request->validated());

    return response()->json($user, 201);
}
```

---

## Validation (MANDATORY)

- Use Form Request classes
- NEVER validate inside controllers

Example:

```php
class StoreUserRequest extends FormRequest
{
    public function rules(): array
    {
        return [
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
        ];
    }
}
```

---

## Service Layer

- All business logic goes here
- Controllers must delegate to services

Example:

```php
class UserService
{
    public function create(array $data): User
    {
        return User::create($data);
    }
}
```

---

## Database / Eloquent

### Rules

- Use Eloquent for standard operations
- Avoid raw queries unless necessary
- Prevent N+1 queries using eager loading

Example:

```php
User::with('roles')->get();
```

---

### Query Optimization

- Select only needed columns
- Use pagination for large datasets

```php
User::select('id', 'name')->paginate(15);
```

---

## API Responses

- Always return JSON
- Use proper HTTP status codes

---

## Authentication (Sanctum)

- Use Sanctum for SPA authentication
- Do not manually handle tokens unless required

---

## Error Handling

- Do not expose internal errors
- Use consistent error responses

---

## Anti-patterns

- Fat controllers
- Business logic inside models
- Repeating validation logic
- Raw SQL everywhere
- Returning unfiltered full models

---

## Behavior

- Extract logic into services if found in controllers
- Suggest Form Requests when validation is inline
- Optimize queries when inefficiencies are detected
