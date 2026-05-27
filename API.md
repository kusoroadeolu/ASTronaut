# Astronaut UI — Decisions & Notes

## Tech Stack

- **React (Vite)** — standalone app, separate from Spring Boot
- **Tailwind CSS** — styling and dark mode
- **shadcn/ui** — component library (sidebar, buttons, badges, dialogs etc.)
- **CodeMirror 6** — syntax highlighting for code view and create/edit editor
- **React Router** — navigation between views
- **Environment** — `VITE_API_BASE_URL=http://localhost:9093` 
- **Language** - Typescript and Tsx

---

## Layout

Three-column structure: a narrow sidebar for navigation and snippet listing, and a wide main panel for content. The sidebar is always visible; the main panel swaps between views depending on context.

The snippet list column is dense and scannable — each item shows the file name, a language badge, and up to 2 tag pills with a "+N more" overflow. Clicking a snippet loads the content view in the main panel without any page reload or jarring transition.

The active snippet in the list gets a solid block highlight (not a border or underline) to make selection unambiguous at a glance.

---

## Sidebar

- App name/logo at top
- "New snippet" button pinned at top
- Search bar (always visible)
- Sort control — `name`, `created_at`, `updated_at` (default)
- Snippet list — each item shows:
    - File name
    - Language badge
    - Up to 2 tag pills, "+N more" overflow for the rest

---

## Search

- Single search bar, hits `GET /snippets/search?query=`
- Supports structured syntax: `tag: utility; language: java; method-name: parse`
- Bare terms (no keyword prefix) match across everything — name, tags, class names, method names
- Keywords: `tag:`, `language:`, `name:`, `method-name:`, `class-name:`
- Delimiter between keywords is `;`

---

## Refresh
- A refresh button that supports the theme of the app
- Not too sure where to put this yet

## Main Panel Views

### Empty / Welcome State
Shown when nothing is selected.

### Snippet View
- Code with syntax highlighting (CodeMirror, read-only), taking up the full panel width — no wasted space
- Metadata — name, language, description, created/updated timestamps
- Tag pills
- Action buttons — edit, delete, compare

### Create / Edit Form
- Fields: file name, description, content (CodeMirror editor), tags
- Language is inferred by the backend — not a user input field

### Diff View
- Unified diff — single column with +/- markers and red/green line overlay
- Backend returns parsed `DiffLine` objects (`ADDED`, `REMOVED`, `UNCHANGED`) — no frontend diff library needed, just render what the backend gives

### Compare Flow
- Hit "compare" on any snippet
- Command palette style modal opens
- Search bar inside modal filters snippet list
- Pick a snippet → diff view opens

---

## Visual Style

Dark theme throughout. The overall aesthetic is tool-first: dense where it needs to be, minimal chrome, nothing decorative that doesn't earn its place.

### Typography
Full monospace stack across the entire UI — not just in the code editor. A refined monospace font (e.g. Geist Mono or Commit Mono) applied to navigation, labels, metadata, and body text reinforces the "developer tool" character without feeling like a terminal. The app name at the top uses a contrasting serif display font for a single moment of visual personality.

### Layout Character
- The snippet list is intentionally compact, styled similarly to a file tree in a code editor — familiar to developers, fast to scan
- The code viewer takes full width in the main panel; the snippet's metadata (name, description, tags, timestamps) is presented in a slim header above the code, not alongside it
- No decorative sidebars, icon rails, or navigation chrome beyond what's functional

### Color Scheme

| Role | Value |
|---|---|
| Background | `#18181b` (zinc-900) — not pure black |
| Code / editor surface | `#1c1c1e` — slightly distinct from background for visual layering |
| Accent | Muted warm white or soft amber — used sparingly for active states and CTAs |
| Primary text | White |
| Secondary text | Mid-gray (descriptions, timestamps) |
| Tertiary text | Darker gray (placeholders) |

- **Active snippet highlight** — solid block, accent-colored, not a border or glow
- **Badges / tags** — slightly lighter background than the surface they sit on, text in the same color family; no rainbow colors, understated
- **Syntax highlighting** — VS Code "One Dark" or "GitHub Dark Default" via CodeMirror; readable without being loud
- **General rule** — no gradients, no glows, no neon; clean and considered, not generated-looking

---

## Running Locally

- Spring Boot: `./mvnw spring-boot:run`
- React: `npm run dev`
- Can be wrapped in a single `start.sh` script