import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/')({ component: App })

function App() {
  return (
    <main className="page-wrap px-4 py-16">
      <section className="island-shell rounded-2xl p-8 sm:p-10">
        <h1 className="display-title text-4xl font-bold text-[var(--sea-ink)] sm:text-5xl">
          Acuafit Frontend
        </h1>
        <p className="mt-3 mb-0 max-w-2xl text-base text-[var(--sea-ink-soft)]">
          Base limpia y funcional lista para empezar el desarrollo del producto.
        </p>
      </section>
    </main>
  )
}
