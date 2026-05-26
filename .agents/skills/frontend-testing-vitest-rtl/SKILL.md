---
name: frontend-testing-vitest-rtl
description: Enforce high-quality unit and integration tests for React applications using Vitest and React Testing Library. Aligned with a feature-based architecture and the project stack: React + TypeScript + TanStack Table + React Hook Form + Zod + shadcn/ui. Tests live next to implementation, cover behavior over internals, and treat the DOM as the source of truth.
---

# Frontend Testing — Vitest + React Testing Library

Stack: Vitest · React Testing Library · @testing-library/user-event · @testing-library/jest-dom  
Scope: components, hooks, form schemas, table columns, services (frontend only)

---

## Stack and Setup

### Current project baseline

This repository already uses:

- `vitest`
- `@testing-library/react`
- `@testing-library/user-event`
- `@testing-library/jest-dom`
- `jsdom`

Use existing versions from `frontend/package.json`; do not force `latest` blindly.

### Active config in this repo

Tests are configured in `frontend/vite.config.ts` under `test`:

- `environment: "jsdom"`
- `globals: true`
- `setupFiles: "./src/setupTests.ts"`

`frontend/src/setupTests.ts` must import:

```ts
import "@testing-library/jest-dom";
```

### Test file location

Tests must live next to implementation files:

```text
src/
  features/
    students/
      components/
        StudentCard.tsx
        StudentCard.test.tsx
      hooks/
        useStudents.ts
        useStudents.test.ts
      schemas/
        enrollment.schema.ts
        enrollment.schema.test.ts
```

---

## Acuafit-specific Rules

- Frontend tests must not encode backend business transitions as frontend logic.
- Validate UI behavior and API integration boundaries, not domain decisions owned by backend.
- For payment/enrollment UI, verify state presentation (`al_dia`, `pendiente`, `vencido`) and error handling.
- Keep service tests focused on request mapping and error mapping; backend remains source of truth.

---

## Core Principles

- Test behavior, not implementation details.
- Treat DOM and accessibility roles as the source of truth.
- Keep tests deterministic and isolated.
- Mock only hard boundaries (network, browser APIs, timers).
- Prefer readability and explicit intent.

---

## Query Priority

Prefer this order:

1. `getByRole`
2. `getByLabelText`
3. `getByPlaceholderText`
4. `getByText`
5. `getByDisplayValue`
6. `getByTestId` only as last resort

```tsx
screen.getByRole("button", { name: /registrar matricula/i })
screen.getByLabelText(/nombre del estudiante/i)
```

Avoid selector-based testing (`querySelector`, class assertions) as primary validation.

---

## Component Tests

Test:

- render by props
- loading/error/empty states
- user interactions
- disabled/aria behavior

Do not test:

- internal state variables
- third-party internals
- styling classes as main assertion

---

## Form Tests (RHF + Zod)

### Schema tests

Test Zod schema independently with `safeParse` for all branches.

### Form behavior tests

- simulate user flow with `userEvent.setup()`
- submit invalid and valid paths
- assert validation messages and submit calls

Do not test RHF internals (`watch`, `setValue`) directly.

---

## TanStack Table Tests

Test column definition behavior in isolation:

- value formatting (`toLocaleString("es-CO")`)
- status badge mapping (`al_dia`, `pendiente`, `vencido`)
- conditional cell rendering

No need to mount full table for every case.

---

## Hook Tests (TanStack Query)

Use `renderHook` with a fresh `QueryClientProvider` per test.

```tsx
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"

export function createTestQueryClient() {
  return new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        gcTime: 0,
      },
      mutations: {
        retry: false,
      },
    },
  })
}

export function createQueryWrapper() {
  const client = createTestQueryClient()
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={client}>{children}</QueryClientProvider>
  )
}
```

For data mocks, prefer mocking service-layer functions called by hooks.

---

## Service/API Tests (Axios)

This project uses Axios service layer (`@/lib/api`), not raw `fetch` as the standard path.

Preferred approach:

- mock `@/lib/api` methods (`get`, `post`, etc.)
- verify endpoint path, payload, and response mapping
- verify thrown errors for non-success flows

```ts
import api from "@/lib/api"
import { vi } from "vitest"

vi.mock("@/lib/api", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

it("requests students list", async () => {
  vi.mocked(api.get).mockResolvedValueOnce({ data: [] })

  await getStudents()

  expect(api.get).toHaveBeenCalledWith("/students")
})
```

---

## Async and Mocking Rules

- Use `userEvent.setup()` (v14+).
- Prefer `findBy*` for awaited elements.
- Use `waitFor` for polling or multi-assertion waits.
- Never use `setTimeout` sleeps to stabilize tests.
- Clear mocks in `afterEach`.
- Restore fake timers after use.

---

## Coverage Expectations

- Initial target: meaningful coverage with priority on critical flows.
- Recommended threshold growth:
  - Phase 1: 60%+
  - Phase 2: 75%+
  - Phase 3: 85%+
- Prioritize branch coverage in forms, status mapping, and error states.

Avoid trivial assertions just to raise percentage.

---

## Anti-patterns

- snapshot-heavy tests for dynamic UI
- test-id as default querying strategy
- class-name assertions as core behavior proof
- massive global setup with hidden dependencies
- over-mocking hooks/components under test

---

## Shipping Checklist

- [ ] Tests live next to implementation
- [ ] Semantic RTL queries are used by default
- [ ] Zod schemas have direct unit tests
- [ ] RHF forms are tested through user interaction
- [ ] TanStack Query hooks are wrapped with test QueryClient
- [ ] Axios service boundary is mocked at `@/lib/api`
- [ ] Status branches (`al_dia`, `pendiente`, `vencido`) are covered
- [ ] Loading, error, and empty states are covered
- [ ] No `setTimeout` workarounds
- [ ] Mocks are cleared/restored per test

```json
{
  "devDependencies": {
    "vitest": "latest",
    "@testing-library/react": "latest",
    "@testing-library/user-event": "latest",
    "@testing-library/jest-dom": "latest",
    "jsdom": "latest"
  }
}
```

### `vitest.config.ts`

```ts
import { defineConfig } from "vitest/config"
import react from "@vitejs/plugin-react"
import path from "path"

export default defineConfig({
  plugins: [react()],
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: ["./src/test/setup.ts"],
    coverage: {
      provider: "v8",
      reporter: ["text", "lcov"],
      exclude: ["src/test/**", "**/*.d.ts", "**/index.ts"],
    },
  },
  resolve: {
    alias: { "@": path.resolve(__dirname, "./src") },
  },
})
```

### `src/test/setup.ts`

```ts
import "@testing-library/jest-dom"
```

### Test file location

Tests live next to the implementation file, same directory:

```
src/
  features/
    students/
      components/
        StudentCard.tsx
        StudentCard.test.tsx
      hooks/
        useStudents.ts
        useStudents.test.ts
      schemas/
        enrollment.schema.ts
        enrollment.schema.test.ts
```

---

## Core Principles

- Test **behavior**, not implementation — assert what the user sees or can do, not how the code achieves it
- Treat the **DOM as source of truth** — query by role, label, and text, not by class or test-id
- Tests must be **deterministic** — same input always produces same output
- Keep tests **isolated** — no shared mutable state between tests
- Avoid **over-mocking** — only mock at the boundary (network, browser APIs, timers)
- Prefer **readability over cleverness** — a test should be understandable without context

---

## Query Priority

Always query in this order (highest to lowest preference):

1. `getByRole` — most accessible, mirrors how AT users navigate
2. `getByLabelText` — for form fields
3. `getByPlaceholderText` — fallback for inputs without labels
4. `getByText` — for visible content
5. `getByDisplayValue` — for filled inputs/selects
6. `getByTestId` — last resort only; prefer semantic queries

```tsx
// Preferred
screen.getByRole("button", { name: /registrar matrícula/i })
screen.getByLabelText(/nombre del estudiante/i)

// Avoid
screen.getByTestId("submit-button")
container.querySelector(".btn-gradient")
```

---

## Component Tests

### What to test

- Correct rendering given props
- Conditional rendering (loading, error, empty states)
- User interactions and their effects on the DOM
- Accessible attributes (aria labels, roles, disabled states)

### What NOT to test

- Internal React state directly
- Implementation details (which function was called, internal variable values)
- Styling (class names, CSS)
- Third-party component internals (shadcn/ui internals)

### Standard component test

```tsx
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { StudentCard } from "./StudentCard"

const defaultProps = {
  nombre: "María García",
  grupo: "Lun/Mié 7:00 am",
  sede: "Aranjuez",
  estado: "al_dia" as const,
}

describe("StudentCard", () => {
  it("renders student information", () => {
    render(<StudentCard {...defaultProps} />)

    expect(screen.getByText("María García")).toBeInTheDocument()
    expect(screen.getByText("Lun/Mié 7:00 am")).toBeInTheDocument()
    expect(screen.getByText("Aranjuez")).toBeInTheDocument()
  })

  it("shows 'Al día' badge when estado is al_dia", () => {
    render(<StudentCard {...defaultProps} estado="al_dia" />)
    expect(screen.getByText("Al día")).toBeInTheDocument()
  })

  it("shows 'Vencido' badge when estado is vencido", () => {
    render(<StudentCard {...defaultProps} estado="vencido" />)
    expect(screen.getByText("Vencido")).toBeInTheDocument()
  })

  it("calls onEdit when edit button is clicked", async () => {
    const user = userEvent.setup()
    const onEdit = vi.fn()

    render(<StudentCard {...defaultProps} onEdit={onEdit} />)
    await user.click(screen.getByRole("button", { name: /editar/i }))

    expect(onEdit).toHaveBeenCalledOnce()
  })

  it("disables edit button when disabled prop is true", () => {
    render(<StudentCard {...defaultProps} disabled />)
    expect(screen.getByRole("button", { name: /editar/i })).toBeDisabled()
  })
})
```

### Loading / error / empty states

```tsx
describe("StudentList", () => {
  it("shows skeleton when loading", () => {
    render(<StudentList isLoading />)
    expect(screen.getByRole("status")).toBeInTheDocument() // aria-busy skeleton
  })

  it("shows error message when fetch fails", () => {
    render(<StudentList error="No se pudo cargar la lista" />)
    expect(screen.getByRole("alert")).toHaveTextContent(/no se pudo cargar/i)
  })

  it("shows empty state when no students", () => {
    render(<StudentList students={[]} />)
    expect(screen.getByText(/sin resultados/i)).toBeInTheDocument()
  })
})
```

---

## Form Tests — React Hook Form + Zod

### Test the Zod schema directly

Schema tests are fast, pure, and should cover all branches independently of the component.

```ts
import { enrollmentSchema } from "./enrollment.schema"

describe("enrollmentSchema", () => {
  const validData = {
    nombre: "María García",
    documento: "10203040",
    sede: "aranjuez",
    grupo: "lun-mie-7",
    mensualidad: 120000,
  }

  it("accepts valid data", () => {
    expect(enrollmentSchema.safeParse(validData).success).toBe(true)
  })

  it("rejects nombre shorter than 2 characters", () => {
    const result = enrollmentSchema.safeParse({ ...validData, nombre: "A" })
    expect(result.success).toBe(false)
    expect(result.error?.issues[0].path).toContain("nombre")
  })

  it("rejects zero or negative mensualidad", () => {
    const result = enrollmentSchema.safeParse({ ...validData, mensualidad: 0 })
    expect(result.success).toBe(false)
    expect(result.error?.issues[0].path).toContain("mensualidad")
  })

  it("coerces string mensualidad to number", () => {
    const result = enrollmentSchema.safeParse({ ...validData, mensualidad: "120000" })
    expect(result.success).toBe(true)
    expect((result as { data: typeof validData }).data.mensualidad).toBe(120000)
  })

  it("requires sede", () => {
    const { sede: _, ...withoutSede } = validData
    const result = enrollmentSchema.safeParse(withoutSede)
    expect(result.success).toBe(false)
  })
})
```

### Test the form component behavior

Focus on user flow and validation feedback — not on RHF internals.

```tsx
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { EnrollmentForm } from "./EnrollmentForm"

describe("EnrollmentForm", () => {
  it("shows validation error when nombre is empty on submit", async () => {
    const user = userEvent.setup()
    render(<EnrollmentForm onSubmit={vi.fn()} />)

    await user.click(screen.getByRole("button", { name: /registrar matrícula/i }))

    expect(await screen.findByText(/el nombre es requerido/i)).toBeInTheDocument()
  })

  it("calls onSubmit with form values when valid", async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn()
    render(<EnrollmentForm onSubmit={onSubmit} />)

    await user.type(screen.getByLabelText(/nombre del estudiante/i), "María García")
    await user.type(screen.getByLabelText(/documento/i), "10203040")
    await user.click(screen.getByRole("combobox", { name: /sede/i }))
    await user.click(screen.getByRole("option", { name: /aranjuez/i }))
    await user.click(screen.getByRole("combobox", { name: /grupo/i }))
    await user.click(screen.getByRole("option", { name: /lun\/mié/i }))
    await user.type(screen.getByLabelText(/mensualidad/i), "120000")

    await user.click(screen.getByRole("button", { name: /registrar matrícula/i }))

    expect(onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({ nombre: "María García", mensualidad: 120000 }),
      expect.anything()
    )
  })

  it("disables submit button while submitting", async () => {
    const user = userEvent.setup()
    // onSubmit that never resolves — simulates in-flight request
    const onSubmit = vi.fn(() => new Promise(() => {}))
    render(<EnrollmentForm onSubmit={onSubmit} />)

    // fill minimum valid data...
    await user.click(screen.getByRole("button", { name: /registrar matrícula/i }))

    expect(screen.getByRole("button", { name: /registrar matrícula/i })).toBeDisabled()
  })
})
```

---

## Table Column Tests — TanStack Table

Test column definitions in isolation: cell rendering, formatting, and badge mapping. No need to mount the full table.

```tsx
import { render, screen } from "@testing-library/react"
import { flexRender, createColumnHelper } from "@tanstack/react-table"
import { columns } from "./students.columns"

// Helper to render a single cell from a column definition
function renderCell<T>(col: typeof columns[number], row: T) {
  const cell = col.cell as (props: { row: { getValue: (key: string) => unknown } }) => React.ReactNode
  const { container } = render(
    <>{cell({ row: { getValue: () => (row as Record<string, unknown>)[col.accessorKey as string] } })}</>
  )
  return container
}

describe("students.columns", () => {
  it("formats mensualidad with locale", () => {
    render(
      <>
        {(columns.find(c => c.accessorKey === "mensualidad")!.cell as Function)({
          row: { getValue: () => 120000 },
        })}
      </>
    )
    expect(screen.getByText(/120\.000/)).toBeInTheDocument()
  })

  it("renders badge-success for al_dia estado", () => {
    render(
      <>
        {(columns.find(c => c.accessorKey === "estado")!.cell as Function)({
          row: { getValue: () => "al_dia" },
        })}
      </>
    )
    expect(screen.getByText("Al día")).toBeInTheDocument()
  })

  it("renders badge-destructive for vencido estado", () => {
    render(
      <>
        {(columns.find(c => c.accessorKey === "estado")!.cell as Function)({
          row: { getValue: () => "vencido" },
        })}
      </>
    )
    expect(screen.getByText("Vencido")).toBeInTheDocument()
  })
})
```

---

## Hook Tests

Use `renderHook` with appropriate providers. Wrap TanStack Query hooks in a `QueryClientProvider` with a fresh client per test.

```tsx
import { renderHook, waitFor } from "@testing-library/react"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { useStudents } from "./useStudents"

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  })
  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  )
}

describe("useStudents", () => {
  it("returns students on successful fetch", async () => {
    vi.spyOn(global, "fetch").mockResolvedValueOnce(
      new Response(JSON.stringify([{ id: "1", nombre: "María García" }]))
    )

    const { result } = renderHook(() => useStudents(), { wrapper: createWrapper() })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))
    expect(result.current.data).toHaveLength(1)
    expect(result.current.data?.[0].nombre).toBe("María García")
  })

  it("sets isError on fetch failure", async () => {
    vi.spyOn(global, "fetch").mockRejectedValueOnce(new Error("Network error"))

    const { result } = renderHook(() => useStudents(), { wrapper: createWrapper() })

    await waitFor(() => expect(result.current.isError).toBe(true))
  })
})
```

---

## Service / API Tests

Test request construction, response mapping, and error handling. Mock `fetch` or `axios` at the module boundary.

```ts
import { getStudents, createEnrollment } from "./students.service"

describe("getStudents", () => {
  afterEach(() => vi.restoreAllMocks())

  it("calls correct endpoint", async () => {
    const fetchSpy = vi.spyOn(global, "fetch").mockResolvedValueOnce(
      new Response(JSON.stringify([]))
    )

    await getStudents()

    expect(fetchSpy).toHaveBeenCalledWith(
      expect.stringContaining("/api/students"),
      expect.any(Object)
    )
  })

  it("maps response to domain type", async () => {
    vi.spyOn(global, "fetch").mockResolvedValueOnce(
      new Response(JSON.stringify([{ id: "1", nombre: "María García", estado: "al_dia" }]))
    )

    const result = await getStudents()

    expect(result[0]).toMatchObject({ id: "1", nombre: "María García" })
  })

  it("throws on non-ok response", async () => {
    vi.spyOn(global, "fetch").mockResolvedValueOnce(
      new Response(null, { status: 500 })
    )

    await expect(getStudents()).rejects.toThrow()
  })
})
```

---

## Async Rules

- Use `userEvent.setup()` — not `userEvent` directly (v14+)
- Prefer `findBy*` (returns promise) over `waitFor(() => getBy*)` for single elements
- Use `waitFor` only for multi-assertion waits or polling conditions
- Do not wrap assertions in `act()` manually — RTL handles it
- Never use `await new Promise(r => setTimeout(r, n))` as a workaround

```tsx
// Preferred
const button = await screen.findByRole("button", { name: /guardar/i })

// Avoid
await waitFor(() => {
  expect(screen.getByRole("button", { name: /guardar/i })).toBeInTheDocument()
})

// Avoid
await new Promise(r => setTimeout(r, 500))
```

---

## Mocking Rules

**Allowed:**
- Network: `vi.spyOn(global, "fetch")` or `vi.mock("axios")`
- Timers: `vi.useFakeTimers()` — always restore with `vi.useRealTimers()` in `afterEach`
- Browser APIs: `IntersectionObserver`, `ResizeObserver`, `matchMedia`
- Module-level mocks for heavy dependencies (e.g. PDF libraries)

**Avoid:**
- Mocking React internals or context values directly
- Mocking simple pure utility functions — just use them
- Mocking the component under test
- Mocking shadcn/ui components — test through them

**Mock structure:**

```ts
// Module mock — at top of file
vi.mock("@/features/students/students.service", () => ({
  getStudents: vi.fn(),
}))

// Per-test setup
import { getStudents } from "@/features/students/students.service"

beforeEach(() => {
  vi.mocked(getStudents).mockResolvedValue([
    { id: "1", nombre: "María García", estado: "al_dia" },
  ])
})

afterEach(() => vi.clearAllMocks())
```

---

## Coverage Expectations

- **Target**: >90% meaningful branch coverage
- Cover all status/state transitions (e.g. `al_dia` → `pendiente` → `vencido`)
- Include realistic edge cases: empty arrays, null values, max-length strings
- Do NOT inflate coverage with trivial assertions (`expect(true).toBe(true)`)
- Do NOT test third-party library behavior

---

## Anti-patterns

| Anti-pattern | Why | Instead |
|---|---|---|
| Snapshot tests for complex UI | Brittle, hides intent | Assert specific text/role/state |
| `getByTestId` everywhere | Bypasses semantics | Use `getByRole`, `getByLabelText` |
| Testing class names | Implementation detail | Test visible output or behavior |
| Massive `beforeAll` setup | Hidden dependencies | Colocate setup inside `describe` |
| `fireEvent` over `userEvent` | Doesn't simulate real events | `userEvent.setup()` |
| Asserting mock call count > 1 without intent | Fragile | Assert the meaningful effect |
| Testing RHF `watch` / `setValue` directly | Implementation detail | Submit the form, check DOM output |
| One `it` block per file | Can't isolate failures | Group with `describe`, one concern per `it` |

---

# Shipping Checklist

- [ ] Test file lives next to implementation (e.g. `StudentCard.test.tsx`)
- [ ] Queries use `getByRole` / `getByLabelText` — no `getByTestId` without justification
- [ ] Zod schemas tested independently of the form component
- [ ] RHF forms tested by simulating user interactions, not by calling RHF methods directly
- [ ] TanStack Table columns tested in isolation (cell rendering, formatting, badge mapping)
- [ ] Async tests use `userEvent.setup()` and `findBy*` or `waitFor`
- [ ] No `setTimeout` workarounds
- [ ] Mocks are minimal, scoped, and cleared in `afterEach`
- [ ] All status/state transitions covered (e.g. `al_dia`, `pendiente`, `vencido`)
- [ ] No snapshot tests for dynamic or data-driven UI
- [ ] Coverage is meaningful — no trivial assertion padding