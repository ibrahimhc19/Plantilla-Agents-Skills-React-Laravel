---
name: ui-skill-acuafit
description: UI engineering skill for building consistent admin dashboards, enrollment forms, student tables, dialogs, and PDF Blade views using React + Vite + TypeScript + Tailwind CSS + shadcn/ui, following the Acuafit design system — a swimming academy with an indigo + cyan palette, dark sidebar, semantic tokens, full dark mode support, reusable utility classes, and production-ready component patterns.
---

# ui-skill-acuafit

Stack: React + Vite + TypeScript + Tailwind CSS + shadcn/ui  
Scope: React views (dashboards, enrollment forms, student tables, payment modals) and Blade views for PDFs (receipts, payment reports, enrollment certificates)

---

## Stack and Constraints

- **React + Vite + TypeScript**: functional components with hooks, explicit typing always.
- **Tailwind CSS v4** with `@theme inline`: use semantic classes (`bg-primary`, `text-sidebar-foreground`). Never use `bg-[hsl(var(--))]` in components. Opacity via modifier: `bg-primary/10`.
- **shadcn/ui**: use built-in primitives (`Button`, `Card`, `Dialog`, `Table`, `Badge`, `Input`, `Select`, `Form`, `Popover`, etc.) as the base layer. Do not reinvent what shadcn already solves.
- **TanStack Table**: all table logic (sorting, filtering, pagination). shadcn/ui `Table`/`TableHeader`/etc. are markup only — logic always lives in `useReactTable`.
- **React Hook Form + Zod**: every form uses `useForm` with `zodResolver`. Schema is typed and exported. Fields are always wrapped in `FormField` → `FormItem` → `FormControl` / `FormMessage`.
- **Blade/PDF**: pure CSS and inline styles only. Tailwind is **not reliable** in dompdf/Browsershot. See dedicated section.

---

## Tailwind v4 — `@theme inline`

This project uses **Tailwind CSS v4**. Key differences from v3:

- Tokens in `:root` use full `hsl()` values: `--primary: hsl(237 43% 30%)`
- The `@theme inline` block maps `--color-primary: var(--primary)` so Tailwind generates `bg-primary`, `text-primary`, etc.
- In `@layer components`, `@apply` uses plain semantic classes (`bg-primary`, `text-sidebar-foreground`) — never `bg-[hsl(var(--primary))]`
- Opacity on tokens: `bg-primary/10`, `text-accent/80` — works because tokens are registered colors in `@theme inline`
- Exceptional inline styles: `style={{ background: "var(--sidebar)" }}` — direct, no extra `hsl()` wrapper

**Never hardcode hex or hsl values in React components.** Always use semantic tokens.

---

## Design System — Tokens

### Full `index.css`

```css
@import url("https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap");
@import "tailwindcss";
@import "tw-animate-css";

@custom-variant dark (&:is(.dark *));

html, body, #root, .app {
  width: 100%;
  font-family: "Inter", sans-serif;
}

@theme inline {
  --radius-sm: calc(var(--radius) - 4px);
  --radius-md: calc(var(--radius) - 2px);
  --radius-lg: var(--radius);
  --radius-xl: calc(var(--radius) + 4px);

  --color-background:                   var(--background);
  --color-foreground:                   var(--foreground);
  --color-card:                         var(--card);
  --color-card-foreground:              var(--card-foreground);
  --color-popover:                      var(--popover);
  --color-popover-foreground:           var(--popover-foreground);
  --color-primary:                      var(--primary);
  --color-primary-foreground:           var(--primary-foreground);
  --color-secondary:                    var(--secondary);
  --color-secondary-foreground:         var(--secondary-foreground);
  --color-muted:                        var(--muted);
  --color-muted-foreground:             var(--muted-foreground);
  --color-accent:                       var(--accent);
  --color-accent-foreground:            var(--accent-foreground);
  --color-destructive:                  var(--destructive);
  --color-border:                       var(--border);
  --color-input:                        var(--input);
  --color-ring:                         var(--ring);
  --color-success:                      var(--success);
  --color-warning:                      var(--warning);
  --color-info:                         var(--info);
  --color-sidebar:                      var(--sidebar);
  --color-sidebar-foreground:           var(--sidebar-foreground);
  --color-sidebar-primary:              var(--sidebar-primary);
  --color-sidebar-primary-foreground:   var(--sidebar-primary-foreground);
  --color-sidebar-accent:               var(--sidebar-accent);
  --color-sidebar-accent-foreground:    var(--sidebar-accent-foreground);
  --color-sidebar-border:               var(--sidebar-border);
  --color-sidebar-ring:                 var(--sidebar-ring);
}

:root {
  --radius: 0.75rem;

  --background:              hsl(0 0% 100%);
  --foreground:              hsl(240 10% 10%);
  --card:                    hsl(0 0% 100%);
  --card-foreground:         hsl(240 10% 10%);
  --popover:                 hsl(0 0% 100%);
  --popover-foreground:      hsl(240 10% 10%);

  /* Primary — indigo from Acuafit logo */
  --primary:                 hsl(237 43% 30%);
  --primary-foreground:      hsl(230 60% 95%);

  --secondary:               hsl(237 30% 95%);
  --secondary-foreground:    hsl(237 43% 25%);

  --muted:                   hsl(237 20% 96%);
  --muted-foreground:        hsl(240 5% 46%);

  /* Accent — cyan from the swimmer figure in the logo */
  --accent:                  hsl(193 75% 57%);
  --accent-foreground:       hsl(193 75% 15%);

  --destructive:             hsl(0 84% 60%);
  --destructive-foreground:  hsl(0 0% 100%);

  /* Semantic extras — registered in @theme inline */
  --success:                 hsl(152 60% 35%);
  --warning:                 hsl(38 92% 50%);
  --info:                    hsl(193 75% 57%);

  --border:                  hsl(237 20% 88%);
  --input:                   hsl(237 20% 88%);
  --ring:                    hsl(237 43% 30%);

  /* Sidebar — always dark in both light and dark mode */
  --sidebar:                 hsl(238 48% 27%);
  --sidebar-foreground:      hsl(230 50% 90%);
  --sidebar-primary:         hsl(193 75% 57%);
  --sidebar-primary-foreground: hsl(0 0% 100%);
  --sidebar-accent:          hsl(238 45% 35%);
  --sidebar-accent-foreground: hsl(230 60% 95%);
  --sidebar-border:          hsl(238 40% 20%);
  --sidebar-ring:            hsl(193 75% 57%);

  /* Gradients */
  --gradient-primary: linear-gradient(135deg, hsl(237 43% 30%), hsl(237 43% 22%));
  --gradient-sidebar: linear-gradient(180deg, hsl(238 48% 27%), hsl(238 48% 18%));
  --gradient-card:    linear-gradient(135deg, hsl(0 0% 100%), hsl(237 20% 97%));

  /* Shadows */
  --shadow-sm:      0 1px 2px 0 hsl(237 43% 30% / 0.06);
  --shadow-md:      0 4px 6px -1px hsl(237 43% 30% / 0.08), 0 2px 4px -2px hsl(237 43% 30% / 0.06);
  --shadow-lg:      0 10px 15px -3px hsl(237 43% 30% / 0.10), 0 4px 6px -4px hsl(237 43% 30% / 0.06);
  --shadow-primary: 0 4px 14px 0 hsl(237 43% 30% / 0.30);
}

.dark {
  --background:              hsl(237 30% 8%);
  --foreground:              hsl(230 40% 94%);
  --card:                    hsl(237 30% 11%);
  --card-foreground:         hsl(230 40% 94%);
  --popover:                 hsl(237 30% 11%);
  --popover-foreground:      hsl(230 40% 94%);

  --primary:                 hsl(237 43% 55%);
  --primary-foreground:      hsl(0 0% 100%);

  --secondary:               hsl(237 25% 18%);
  --secondary-foreground:    hsl(230 40% 90%);

  --muted:                   hsl(237 20% 16%);
  --muted-foreground:        hsl(230 15% 62%);

  --accent:                  hsl(193 75% 52%);
  --accent-foreground:       hsl(193 75% 10%);

  --destructive:             hsl(0 62% 40%);
  --destructive-foreground:  hsl(0 0% 100%);

  --success:                 hsl(152 55% 42%);
  --warning:                 hsl(38 85% 52%);
  --info:                    hsl(193 75% 52%);

  --border:                  hsl(237 20% 20%);
  --input:                   hsl(237 20% 20%);
  --ring:                    hsl(237 43% 55%);

  --sidebar:                 hsl(238 48% 14%);
  --sidebar-foreground:      hsl(230 40% 88%);
  --sidebar-primary:         hsl(193 75% 52%);
  --sidebar-primary-foreground: hsl(0 0% 100%);
  --sidebar-accent:          hsl(238 40% 20%);
  --sidebar-accent-foreground: hsl(230 40% 90%);
  --sidebar-border:          hsl(238 35% 18%);
  --sidebar-ring:            hsl(193 75% 52%);

  --chart-1: hsl(237 43% 55%);
  --chart-2: hsl(193 75% 52%);
  --chart-3: hsl(160 55% 45%);
  --chart-4: hsl(30 80% 55%);
  --chart-5: hsl(340 70% 55%);
}

@layer base {
  * { @apply border-border outline-ring/50; }
  body { @apply bg-background text-foreground; }
}

@layer components {

  /* Stat card */
  .stat-card {
    @apply bg-card rounded-xl border border-border p-5;
    box-shadow: var(--shadow-sm);
  }

  /* Table header cell */
  .table-header {
    @apply text-xs font-semibold uppercase tracking-wider text-muted-foreground;
  }

  /* Sidebar nav items */
  .sidebar-item {
    @apply flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors;
  }
  .sidebar-item-active {
    @apply bg-sidebar-accent text-sidebar-accent-foreground;
  }
  .sidebar-item-inactive {
    @apply text-sidebar-foreground hover:bg-sidebar-accent/60 hover:text-sidebar-accent-foreground;
  }

  /* Form input */
  .input-field {
    @apply border-input bg-background focus-visible:ring-2 focus-visible:ring-ring/50;
  }

  /* Modal backdrop */
  .modal-overlay {
    @apply fixed inset-0 bg-black/50 backdrop-blur-sm z-50;
  }

  /* Page header */
  .page-header {
    @apply flex flex-col gap-1 pb-4 border-b border-border;
  }
  .page-title {
    @apply text-2xl font-bold tracking-tight text-foreground;
  }
  .page-description {
    @apply text-sm text-muted-foreground;
  }

  /* Status badges */
  .badge-success {
    @apply border-success bg-success/10 text-success font-medium;
  }
  .badge-warning {
    @apply border-warning bg-warning/10 text-warning font-medium;
  }
  .badge-destructive {
    @apply border-destructive bg-destructive/10 text-destructive font-medium;
  }
  .badge-info {
    @apply border-info bg-info/10 text-info font-medium;
  }
  /* Acuafit-specific */
  .badge-enrolled {
    @apply border-primary bg-primary/10 text-primary font-medium;
  }
  .badge-inactive {
    @apply border-border bg-muted text-muted-foreground font-medium;
  }

  /* Primary CTA button */
  .btn-gradient {
    @apply text-primary-foreground font-medium transition-opacity hover:opacity-90;
    background: var(--gradient-primary);
    box-shadow: var(--shadow-primary);
  }

  /* Premium card surface */
  .card-gradient {
    @apply rounded-xl border border-border;
    background: var(--gradient-card);
    box-shadow: var(--shadow-sm);
  }

  /* Decorative cyan underline on stat cards */
  .accent-bar {
    @apply h-[3px] w-10 rounded-full mt-2 bg-accent;
  }
}
```

---

### Main Palette Reference

| Semantic Token        | Light value                | Tailwind class                | Usage |
|-----------------------|----------------------------|-------------------------------|-------|
| `--primary`           | `hsl(237 43% 30%)`         | `bg-primary` / `text-primary` | Indigo from logo — CTAs, links, active icons |
| `--accent`            | `hsl(193 75% 57%)`         | `bg-accent` / `text-accent`   | Cyan from swimmer — highlights, accent bars |
| `--background`        | `hsl(0 0% 100%)`           | `bg-background`               | Page background (light mode) |
| `--foreground`        | `hsl(240 10% 10%)`         | `text-foreground`             | Primary text |
| `--card`              | `hsl(0 0% 100%)`           | `bg-card`                     | Card surface |
| `--muted`             | `hsl(237 20% 96%)`         | `bg-muted`                    | Soft surfaces, table headers |
| `--muted-foreground`  | `hsl(240 5% 46%)`          | `text-muted-foreground`       | Secondary text, placeholders |
| `--border`            | `hsl(237 20% 88%)`         | `border-border`               | Input/card/divider borders |
| `--destructive`       | `hsl(0 84% 60%)`           | `text-destructive`            | Errors, delete actions |
| `--success`           | `hsl(152 60% 35%)`         | `text-success`                | Payment received, active enrollment |
| `--warning`           | `hsl(38 92% 50%)`          | `text-warning`                | Pending payment, expiring soon |
| `--info`              | `hsl(193 75% 57%)`         | `text-info`                   | Informational — same hue as accent |

### Sidebar Tokens (always dark, both modes)

| Token                         | Light value        | Tailwind class                   | Usage |
|-------------------------------|--------------------|----------------------------------|-------|
| `--sidebar`                   | `hsl(238 48% 27%)` | `bg-sidebar`                     | Sidebar background |
| `--sidebar-foreground`        | `hsl(230 50% 90%)` | `text-sidebar-foreground`        | Menu text and icons |
| `--sidebar-primary`           | `hsl(193 75% 57%)` | `bg-sidebar-primary`             | Active item (cyan) |
| `--sidebar-accent`            | `hsl(238 45% 35%)` | `bg-sidebar-accent`              | Hover / active item background |
| `--sidebar-accent-foreground` | `hsl(230 60% 95%)` | `text-sidebar-accent-foreground` | Text in hover/active state |
| `--sidebar-border`            | `hsl(238 40% 20%)` | `border-sidebar-border`          | Internal dividers |

> **Note**: the sidebar token is `--sidebar` (not `--sidebar-background`). `@theme inline` maps it to `--color-sidebar`, which generates `bg-sidebar`. If using shadcn/ui `SidebarProvider`, ensure its internal CSS also points to `--sidebar`.

---

## Typography

- **Inter**: body, labels, forms, tables — weights 300/400/500/600/700/800
- **font-mono** (Tailwind built-in): numeric values (payments, IDs, dates), totals

```tsx
<span className="font-mono text-sm">$ 120,000</span>
<p className="text-sm text-muted-foreground">Descripción del campo</p>
```

### Type Scale

| Usage          | Tailwind Classes |
|----------------|-----------------|
| Page title     | `text-2xl font-bold tracking-tight` |
| Subtitle       | `text-muted-foreground` |
| Form label     | `text-sm font-medium` |
| Table body     | `text-sm` |
| Table header   | `text-xs font-semibold uppercase tracking-wider text-muted-foreground` |
| Monetary value | `font-mono text-sm` or `font-mono font-semibold` |
| Badge          | `text-xs font-medium` |

---

## Common UI Patterns

### Dialog and Alert Footer Actions (Required Pattern)

Use this footer action order consistently across create/edit flows and delete confirmations.

- Standard Dialog footer (create/edit):
  - Left side: destructive action (`Eliminar`) when present.
  - Right side: `Cancelar`, then primary submit (`Guardar` / `Actualizar`).
  - Desktop layout must be extreme-to-extreme (`justify-between`) when delete is present.
- Delete confirmation (`AlertDialog`):
  - Left side: `Eliminar`.
  - Right side: `Cancelar`.
  - Buttons separated extreme-to-extreme on desktop.

Visual variants:

- `Eliminar` in dialogs and delete alerts: use destructive-outline appearance (`outline` + `destructive` semantic classes).
- `Cancelar` in dialogs and delete alerts must use the **same classes** for consistency and dark-mode contrast:
  - `bg-muted text-foreground hover:bg-muted/80 hover:text-foreground`
- Primary submit (`Guardar` / `Actualizar`): default button variant.

Implementation note:

- When using shadcn `DialogFooter` or `AlertDialogFooter`, override defaults with explicit wrappers/classes so action positions are deterministic on desktop, while keeping mobile stacking.

### Dialog and Modal Text Contrast (Required Pattern)

Apply this baseline in all `Dialog`, `AlertDialog`, and modal-like surfaces:

- `DialogContent`: include `text-foreground` to inherit readable body text.
- `DialogTitle`: explicit `text-foreground`.
- `DialogDescription`: explicit `text-muted-foreground`.

Form field readability inside dialogs/modals:

- Inputs should keep typed text visible in both themes:
  - `text-foreground`
- Placeholders should remain secondary but readable:
  - `placeholder:text-muted-foreground`

Reference implementation: Venues module (`VenuesDialog` + `VenuesForm`) is the canonical pattern for create/edit + delete-confirmation flows.

### Page Header

```tsx
<div className="space-y-6">
  <div className="page-header">
    <h1 className="page-title">Estudiantes</h1>
    <p className="page-description">Gestión de matrículas y grupos</p>
  </div>
</div>
```

### KPI Stat Card

```tsx
<div className="stat-card">
  <div className="flex items-center justify-between mb-2">
    <span className="text-sm font-medium text-muted-foreground">
      Estudiantes activos
    </span>
    <Users className="h-4 w-4 text-muted-foreground" />
  </div>
  <div className="text-2xl font-bold font-mono">148</div>
  <div className="accent-bar" />
  <p className="text-xs text-muted-foreground mt-1">+8 este mes</p>
</div>
```

### Status Badges — Acuafit domain

```tsx
<Badge variant="outline" className="badge-success">Al día</Badge>
<Badge variant="outline" className="badge-warning">Pendiente</Badge>
<Badge variant="outline" className="badge-destructive">Vencido</Badge>
<Badge variant="outline" className="badge-enrolled">Matriculado</Badge>
<Badge variant="outline" className="badge-inactive">Inactivo</Badge>
<Badge variant="outline" className="badge-info">En proceso</Badge>
```

### Table — TanStack Table + shadcn/ui

Tables use **TanStack Table** for logic (sorting, filtering, pagination) and shadcn/ui primitives (`Table`, `TableHeader`, etc.) for markup only. Never use shadcn/ui Table standalone without TanStack.

**Column definitions:**

```tsx
import { ColumnDef } from "@tanstack/react-table"
import { Badge } from "@/components/ui/badge"

type Estudiante = {
  id: string
  nombre: string
  grupo: string
  sede: string
  mensualidad: number
  estado: "al_dia" | "pendiente" | "vencido"
}

const statusBadge: Record<Estudiante["estado"], { label: string; className: string }> = {
  al_dia:    { label: "Al día",    className: "badge-success" },
  pendiente: { label: "Pendiente", className: "badge-warning" },
  vencido:   { label: "Vencido",   className: "badge-destructive" },
}

export const columns: ColumnDef<Estudiante>[] = [
  {
    accessorKey: "nombre",
    header: "Estudiante",
    cell: ({ row }) => (
      <span className="font-medium">{row.getValue("nombre")}</span>
    ),
  },
  {
    accessorKey: "grupo",
    header: "Grupo",
  },
  {
    accessorKey: "sede",
    header: "Sede",
  },
  {
    accessorKey: "mensualidad",
    header: () => <span className="text-right block">Mensualidad</span>,
    cell: ({ row }) => (
      <span className="font-mono block text-right">
        $ {(row.getValue("mensualidad") as number).toLocaleString("es-CO")}
      </span>
    ),
  },
  {
    accessorKey: "estado",
    header: "Estado",
    cell: ({ row }) => {
      const { label, className } = statusBadge[row.getValue("estado") as Estudiante["estado"]]
      return <Badge variant="outline" className={className}>{label}</Badge>
    },
  },
]
```

**Table component:**

```tsx
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  flexRender,
  SortingState,
} from "@tanstack/react-table"
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table"

interface DataTableProps<TData> {
  columns: ColumnDef<TData, unknown>[]
  data: TData[]
}

export function DataTable<TData>({ columns, data }: DataTableProps<TData>) {
  const [sorting, setSorting] = React.useState<SortingState>([])

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    onSortingChange: setSorting,
    state: { sorting },
  })

  return (
    <div className="rounded-xl border border-border overflow-hidden">
      <Table>
        <TableHeader>
          {table.getHeaderGroups().map((headerGroup) => (
            <TableRow key={headerGroup.id} className="bg-muted/50 hover:bg-muted/50">
              {headerGroup.headers.map((header) => (
                <TableHead key={header.id} className="table-header">
                  {header.isPlaceholder ? null
                    : flexRender(header.column.columnDef.header, header.getContext())}
                </TableHead>
              ))}
            </TableRow>
          ))}
        </TableHeader>
        <TableBody>
          {table.getRowModel().rows.length ? (
            table.getRowModel().rows.map((row) => (
              <TableRow key={row.id} className="hover:bg-muted/50 transition-colors">
                {row.getVisibleCells().map((cell) => (
                  <TableCell key={cell.id}>
                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                  </TableCell>
                ))}
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={columns.length} className="text-center text-muted-foreground py-8">
                Sin resultados
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </div>
  )
}
```

---

### Form — React Hook Form + Zod + shadcn/ui Form

Every form uses **RHF** for state, **Zod** for validation schema, and shadcn/ui `Form`, `FormField`, `FormItem`, `FormLabel`, `FormControl`, `FormMessage` primitives for markup. Never use `Input` or `Select` standalone — always wrap them inside `FormField`.

**Zod schema:**

```tsx
import { z } from "zod"

export const enrollmentSchema = z.object({
  nombre:      z.string().min(2, "El nombre es requerido"),
  documento:   z.string().min(5, "Documento inválido"),
  sede:        z.string({ required_error: "Selecciona una sede" }),
  grupo:       z.string({ required_error: "Selecciona un grupo" }),
  mensualidad: z.coerce.number().positive("Debe ser mayor a 0"),
})

export type EnrollmentFormValues = z.infer<typeof enrollmentSchema>
```

**Form component:**

```tsx
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import {
  Form, FormControl, FormField, FormItem, FormLabel, FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select"

export function EnrollmentForm() {
  const form = useForm<EnrollmentFormValues>({
    resolver: zodResolver(enrollmentSchema),
    defaultValues: { nombre: "", documento: "", mensualidad: 0 },
  })

  function onSubmit(values: EnrollmentFormValues) {
    // API call / mutation
    console.log(values)
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">

        <FormField control={form.control} name="nombre" render={({ field }) => (
          <FormItem>
            <FormLabel>Nombre del estudiante</FormLabel>
            <FormControl>
              <Input className="input-field" placeholder="Nombre completo" {...field} />
            </FormControl>
            <FormMessage />
          </FormItem>
        )} />

        <FormField control={form.control} name="sede" render={({ field }) => (
          <FormItem>
            <FormLabel>Sede</FormLabel>
            <Select onValueChange={field.onChange} defaultValue={field.value}>
              <FormControl>
                <SelectTrigger className="input-field">
                  <SelectValue placeholder="Seleccionar sede" />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                <SelectItem value="aranjuez">Aranjuez</SelectItem>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        )} />

        <FormField control={form.control} name="grupo" render={({ field }) => (
          <FormItem>
            <FormLabel>Grupo</FormLabel>
            <Select onValueChange={field.onChange} defaultValue={field.value}>
              <FormControl>
                <SelectTrigger className="input-field">
                  <SelectValue placeholder="Día y hora" />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                <SelectItem value="lun-mie-7">Lun/Mié 7:00 am</SelectItem>
                <SelectItem value="mar-jue-6">Mar/Jue 6:00 pm</SelectItem>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        )} />

        <FormField control={form.control} name="mensualidad" render={({ field }) => (
          <FormItem>
            <FormLabel>Mensualidad</FormLabel>
            <FormControl>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground text-sm">$</span>
                <Input className="input-field pl-7 font-mono" placeholder="0" {...field} />
              </div>
            </FormControl>
            <FormMessage />
          </FormItem>
        )} />

        <Button type="submit" className="btn-gradient w-full">
          Registrar matrícula
        </Button>

      </form>
    </Form>
  )
}
```

### Primary CTA Button

```tsx
<Button className="btn-gradient">
  Registrar matrícula
</Button>
```

### Sidebar — base structure

```tsx
<aside className="w-56 min-h-screen flex flex-col bg-sidebar">

  {/* Brand header */}
  <div className="px-4 py-5 border-b border-sidebar-border">
    <p className="text-sm font-semibold text-sidebar-foreground">Acuafit</p>
    <p className="text-xs mt-0.5 text-sidebar-foreground/55">Academia de natación</p>
  </div>

  {/* Navigation */}
  <nav className="flex-1 p-3 space-y-0.5">
    <a href="#" className="sidebar-item sidebar-item-active">
      <LayoutDashboard className="h-4 w-4" />
      Dashboard
    </a>
    <a href="#" className="sidebar-item sidebar-item-inactive">
      <Users className="h-4 w-4" />
      Estudiantes
    </a>
    <a href="#" className="sidebar-item sidebar-item-inactive">
      <CalendarDays className="h-4 w-4" />
      Grupos
    </a>
    <a href="#" className="sidebar-item sidebar-item-inactive">
      <CreditCard className="h-4 w-4" />
      Pagos
    </a>
  </nav>
</aside>
```

---

## Dark Mode

Fully supported via `.dark` on `<html>`.

- Sidebar tokens have their own values in both `:root` and `.dark` — they remain dark in both modes by design.
- Always use semantic classes: `bg-card`, `text-foreground`, `border-border`, `bg-sidebar`, etc.
- Never hardcode hex, hsl, or `bg-[hsl(var(--))]` in React components — in Tailwind v4 with `@theme inline` semantic classes resolve the mode automatically.
- Opacity on tokens: `bg-primary/10`, `text-accent/70`.

---

## Icons

Use **Lucide React**.

- Inline icons: `h-4 w-4`
- Button icons: `h-5 w-5`
- Decorative card icons: `h-8 w-8`

Common domain icons: `Users`, `CalendarDays`, `CreditCard`, `Waves`, `MapPin`, `LayoutDashboard`, `FileText`, `UserPlus`, `BadgeCheck`.

---

# Blade Views

## Regular Blade views (non-PDF)

For Blade views rendered in the browser, **Tailwind can be used** if compiled in the Laravel project. Use the same semantic classes: `bg-card`, `text-foreground`, `border-border`, etc.

---

## Blade PDF views — strict rules

PDFs are generated with dompdf or Browsershot. **Do not use Tailwind here.** Pure CSS and inline styles only.

### Hex token reference for PDF

```css
/* Primary — indigo from logo */
--primary:       #2C3073;   /* hsl(237 43% 30%) */
--primary-light: #ECEEFF;

/* Accent — cyan from swimmer */
--accent:        #3FC5E6;   /* hsl(193 75% 57%) */
--accent-light:  #E0F6FC;

/* Surfaces */
--background:    #F4F5FA;
--card:          #FFFFFF;
--foreground:    #18191F;
--muted-fg:      #737588;
--border:        #D5D7E8;

/* Semantic */
--success:       #1A7A4A;
--warning:       #D97706;
--destructive:   #DC2626;
--info:          #0A8AA8;
```

### PDF base layout — receipt / report

```html
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }

  body {
    font-family: "DejaVu Sans", sans-serif;
    font-size: 12px;
    color: #18191F;
    background: #F4F5FA;
  }

  .pdf-page {
    background: #FFFFFF;
    margin: 0 auto;
    padding: 32px 36px;
    max-width: 720px;
  }

  /* Brand header */
  .pdf-header {
    background: #2C3073;
    color: #ECEEFF;
    padding: 20px 24px;
    border-radius: 4px 4px 0 0;
    margin-bottom: 0;
  }
  .pdf-header-title {
    font-size: 18px;
    font-weight: bold;
    letter-spacing: 0.02em;
  }
  .pdf-header-subtitle {
    font-size: 11px;
    color: #9BA3CC;
    margin-top: 2px;
  }

  /* Cyan accent bar below header */
  .pdf-accent-bar {
    height: 3px;
    background: #3FC5E6;
    margin-bottom: 24px;
  }

  /* Document metadata (receipt number, date, etc.) */
  .pdf-meta-table {
    width: 100%;
    margin-bottom: 20px;
    border-collapse: collapse;
  }
  .pdf-meta-table td {
    padding: 4px 0;
    font-size: 11px;
  }
  .pdf-meta-label {
    color: #737588;
    width: 130px;
  }
  .pdf-meta-value {
    font-weight: 600;
    font-family: "Courier New", monospace;
  }

  /* Main data table */
  .pdf-table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
  }
  .pdf-table th {
    background: #ECEEFF;
    color: #2C3073;
    font-size: 10px;
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    padding: 8px 10px;
    text-align: left;
    border-bottom: 1px solid #D5D7E8;
  }
  .pdf-table td {
    padding: 8px 10px;
    font-size: 11px;
    border-bottom: 1px solid #EBEBF5;
    vertical-align: top;
  }
  .pdf-table tr:last-child td { border-bottom: none; }

  /* Totals row */
  .pdf-total-row { background: #F4F5FA; }
  .pdf-total-row td {
    font-weight: bold;
    color: #2C3073;
    font-family: "Courier New", monospace;
    border-top: 2px solid #D5D7E8;
  }

  /* Status badges */
  .badge {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 3px;
    font-size: 10px;
    font-weight: bold;
  }
  .badge-success  { background: #D1FAE5; color: #1A7A4A; }
  .badge-warning  { background: #FEF3C7; color: #D97706; }
  .badge-danger   { background: #FEE2E2; color: #DC2626; }
  .badge-enrolled { background: #ECEEFF; color: #2C3073; }
  .badge-info     { background: #E0F6FC; color: #0A8AA8; }

  /* Footer */
  .pdf-footer {
    margin-top: 32px;
    padding-top: 12px;
    border-top: 1px solid #D5D7E8;
    font-size: 10px;
    color: #737588;
    text-align: center;
  }
  .pdf-footer-brand { color: #2C3073; font-weight: bold; }
</style>
</head>
<body>
<div class="pdf-page">

  <div class="pdf-header">
    <div class="pdf-header-title">Acuafit</div>
    <div class="pdf-header-subtitle">Academia de natación — Nadar para vivir</div>
  </div>
  <div class="pdf-accent-bar"></div>

  {{-- Document metadata --}}
  <table class="pdf-meta-table">
    <tr>
      <td class="pdf-meta-label">Documento</td>
      <td class="pdf-meta-value">{{ $document_type }}</td>
    </tr>
    <tr>
      <td class="pdf-meta-label">Nro.</td>
      <td class="pdf-meta-value">{{ $number }}</td>
    </tr>
    <tr>
      <td class="pdf-meta-label">Fecha</td>
      <td class="pdf-meta-value">{{ $date }}</td>
    </tr>
    <tr>
      <td class="pdf-meta-label">Sede</td>
      <td class="pdf-meta-value">{{ $sede }}</td>
    </tr>
  </table>

  {{-- Main content --}}
  {{-- ... data table, payment summary, etc. --}}

  <div class="pdf-footer">
    Generado el {{ now()->format('d/m/Y H:i') }} &nbsp;|&nbsp;
    <span class="pdf-footer-brand">Acuafit</span> — Sistema de gestión académica
  </div>

</div>
</body>
</html>
```

### PDF important notes

- Safe font: `DejaVu Sans` (bundled with dompdf)
- Numeric/monetary values: `Courier New`, monospace
- **No flexbox or grid** — use `<table>` or `display: inline-block`
- Max `border-radius`: `4px`
- Images: absolute filesystem paths or base64
- The `#2C3073` header and `#3FC5E6` accent bar below it anchor Acuafit's brand identity across all documents

---

## Domain Vocabulary — Acuafit

Use this terminology consistently in labels, placeholders, page titles, and messages.

| Concept | Correct term |
|---------|-------------|
| Group / class | **Grupo** (defined by day + time slot) |
| Location / branch | **Sede** |
| Registration / enrollment | **Matrícula** |
| Level assessment | Done in person by the instructor — not captured in forms |
| Monthly fee | **Mensualidad** |
| Active payment status | **Al día** |
| Unpaid status | **Pendiente** |
| Overdue status | **Vencido** |
| Active student | **Matriculado** |
| Online payment gateway | **Wompi** |
| Manual bank transfer | **Consignación bancaria** |

---

# Shipping Checklist

**React / TSX**
- [ ] Colors via semantic tokens (`bg-card`, `text-foreground`, `bg-primary/10`) — no hardcoded hex or `bg-[hsl(var(--))]`
- [ ] Sidebar uses `bg-sidebar`, `text-sidebar-foreground`, `border-sidebar-border`
- [ ] Tables use TanStack Table for logic + shadcn/ui primitives for markup only
- [ ] Columns typed with `ColumnDef<T>` — domain types defined in the columns file
- [ ] Forms use RHF (`useForm` + `zodResolver`) with an explicit Zod schema
- [ ] Form fields wrapped in `FormField` → `FormItem` → `FormLabel` / `FormControl` / `FormMessage`
- [ ] Numeric and monetary values use `font-mono` + `toLocaleString("es-CO")`
- [ ] Statuses use `.badge-success / -warning / -destructive / -enrolled / -inactive`
- [ ] Inputs use `.input-field`
- [ ] Page uses `.page-header`, `.page-title`, `.page-description`
- [ ] Cyan accent bar present on relevant stat cards
- [ ] Works in dark mode (test with `.dark` on `<html>`)

**Blade PDF**
- [ ] Pure CSS only — no Tailwind, no RHF/Zod
- [ ] No flexbox or grid — `<table>` or `display: inline-block` only
- [ ] Font: `DejaVu Sans`; monospace: `Courier New`
- [ ] Max `border-radius`: `4px`
- [ ] Indigo header `#2C3073` + cyan bar `#3FC5E6` present
- [ ] Domain vocabulary is consistent (see table above)