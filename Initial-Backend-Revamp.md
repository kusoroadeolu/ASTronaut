# ASTronaut Revamp — Planning Doc

## The Problem With The Old Version
- Auth was a blocker just to use your own local tool
- UI was ugly and missing features that actually mattered
- Too much infrastructure for what is essentially a personal snippet organizer (PostgreSQL, Redis, Spring Security, JWT, user management)
- High memory usage running in the background

---

## The Goal
> *"ASTronaut but it actually works the way I want it to"*

A lightweight, local-first Java snippet organizer that uses GitHub Gists as storage and UIGen for the UI. No login screen, no DB, no Redis — just open it and it works.

---

## Core Appeals
- **No auth blocker** — GitHub PAT in `.env`, that's it
- **Way better UI** — UIGen on top of the OpenAPI spec
- **Local feel** — instant, snappy, runs quietly in the background
- **Low memory** — no PostgreSQL, no Redis, no Spring Security
- **Gists as storage** — snippets live on GitHub too, accessible anywhere
- **Proper search** — by language, tags, metadata (stuff GitHub Gists can't do natively)
- **Features you'll actually use** — no bloat

---

## What's Being Removed
- Auth system (JWT, Spring Security, login/register flow)
- `AuthController` and `AppUserController` entirely
- User management (update email, password, delete account)
- PostgreSQL + JPA
- Redis + rate limiting
- Draft flag (useless)
- Admin role (pointless solo)
- Deep metadata extraction (field annotations, class field details — overkill)

---

## What's Being Kept
- Spring Boot backend (reworked service layer)
- Java parser for metadata extraction (simplified)
- Diff comparison between two snippets
- Fuzzy search toggle (reimplemented in-memory — no longer DB dependent)
- Async parsing on snippet create/update

---

## What's Being Added
- GitHub Gists as storage backend
- Use count / last used timestamp (tracked in index)
- UIGen for the frontend

---

## Architecture

### Storage: GitHub Gists API
- One Gist per snippet
- Each Gist contains:
    - `file-name.java` — the actual code
- Gist description = snippet name (readable on GitHub.com directly)
- Auth via GitHub PAT in `.env` — **not OAuth**
    - OAuth is for multi-user apps where other people log in with their GitHub account
    - PAT is simpler and correct here since it's just you
    - Generate a PAT on GitHub with `gist` scope, add `GITHUB_TOKEN=ghp_xxxxx` to `.env`
    - Spring passes it as `Authorization: Bearer ghp_xxxxx` on every Gists API call
    - No callback URLs, no token refresh, no auth flow

### The Index File
A single dedicated local file (identified by description `"index.json"`) acts as a metadata index. This avoids fetching every Gist on every search.

**Index structure:**
```json
{
  "abc123": {
    "gistId": "abc123",
    "name": "Spring filter chain",
    "language": "JAVA",
    "tags": ["spring", "security"],
    "methodNames": ["doFilter", "configure"],
    "classNames": ["SecurityConfig"],
    "createdAt": "2025-01-10T10:00:00",
    "updatedAt": "2025-01-10T10:00:00"
  }
}
```

### Index Lifecycle
| Event | Action |
|---|---|
| App startup | Find index Gist by description, create it if missing |
| Create snippet | Create Gist → run parser → add entry to index |
| Update snippet | Patch Gist → re-run parser → update index entry |
| Delete snippet | Delete Gist → remove entry from index |
| Search | Fetch index → filter in memory → fetch only matched Gists |
| Get all | Read index only, no need to fetch every Gist |
| Get one | Fetch specific Gist by ID |

---

## Search & Fuzzy Search
Old fuzzy search used Spring Data Specifications with `LIKE` queries on the DB — that's gone. Since search now filters the in-memory index, it's reimplemented in Java:

- **Exact/direct:** `equalsIgnoreCase()`, `tags.contains()`
- **Fuzzy:** `toLowerCase().contains()`, `tags.stream().anyMatch(t -> t.contains(partial))`

Fuzzy toggle still exists, just switches between exact and `contains()` style matching instead of `=` vs `LIKE` in SQL. Likely faster than the old approach for personal use scale (hundreds of snippets, not thousands).

---


- Class names
- Method names
- Method annotations
- Tags (user defined)
- Language

Everything else (field annotations, class fields, class annotations depth) — dropped.

---

## API Endpoints (New Spec)

### Snippets
| Method   | Path | Description |
|----------|---|---|
| `GET`    | `/snippets` | List all snippets (from index) |
| `POST`   | `/snippets` | Create snippet (creates Gist + updates index) |
| `GET`    | `/snippets/{id}` | Get snippet by Gist ID |
| `PATCH`  | `/snippets/{id}` | Update snippet (patches Gist + updates index) |
| `DELETE` | `/snippets/{id}` | Delete snippet (deletes Gist + removes from index) |
| `POST`   | `/snippets/filter` | Search/filter snippets (in-memory on index) |
| `GET`    | `/snippets/{id}/compare/{comparingToId}` | Diff two snippets |



---

## Features (What The UI Needs To Support)
- View all snippets with previews
- Create / edit / delete snippets
- Copy snippet to clipboard
- Syntax highlighting in view and diff viewer
- Markdown formatting for extra notes
- Search by name, tag, language, method name, class name
- Fuzzy search toggle (disabled by default)
- Diff comparison between any two snippets
- Star / unstar snippets
- Starred snippets view
- Use count / last used display

---

## What's Not Needed In The UI
- Login / register pages
- Settings page (no user account to manage)
- Admin anything

---

## Tech Stack
| Layer | Tech |
|---|---|
| Backend | Spring Boot (reworked, no Security/Redis/JPA) |
| Storage | GitHub Gists API |
| Auth | GitHub PAT via `.env` (not OAuth — PAT is sufficient for personal local use) |
| Metadata parsing | JavaParser (simplified visitors) |
| Diff | java-diff-utils (unchanged) |
| Frontend | UIGen (runtime rendering from OpenAPI spec) |

---

## Next Steps
1. Write the new OpenAPI spec (drives everything else)
2. Rework the Spring service layer (swap DB calls for GitHub API calls)
3. Simplify the parser visitors (drop unused metadata)
4. Configure UIGen on top of the spec
5. Test end to end