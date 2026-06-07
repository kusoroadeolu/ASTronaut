\*This is a submission for the [GitHub Finish-Up-A-Thon Challenge](https://dev.to/challenges/github-2026-05-21)\*

## What I Revived

**ASTronaut** is a personal code snippet manager that uses GitHub Gists as its storage backend. The name is a pun — it literally builds an AST (Abstract Syntax Tree) of your Java snippets using JavaParser, extracting class names and method names so you can search by structure, not just by file name.

The idea is simple: stop losing useful code snippets to random notes apps or buried Gist pages. ASTronaut gives you a proper UI to create, search, edit, diff, and organize your snippets — while keeping everything synced to GitHub Gists so your snippets are accessible anywhere, not locked to one machine.

Key features:
- Full CRUD for code snippets, backed by GitHub Gists
- Structured search by `language:`, `tag:`, `method-name:`, `class-name:`, and more
- Diff comparison between any two snippets, powered by `java-diff-utils` on the backend
- AST parsing for Java snippets — class and method names extracted automatically
- In-memory snippet index for fast search without hitting the GitHub API on every query
- Single JAR distribution — frontend bundled into Spring Boot's static folder, one `java -jar` to run everything

---

## Demo
[ Watch the demo](videos/astronaut-demo.mp4)

---

## The Revamp

The original ASTronaut was... a lot. It had PostgreSQL, Redis, Spring Security, JWT auth, a full login/register flow, user management, admin roles, rate limiting — the works. For what was essentially a personal local tool that only I would ever use.

It never shipped. Not because it didn't work, but because every time I sat down to use it, I had to spin up a database, a Redis instance, deal with tokens, and it just felt like too much friction for something that was supposed to save me time. The motivation drained fast.

The revamp had one goal: *make it actually feel like a tool, not a project.*

Here's what got cut:
- The entire auth system — Spring Security, JWT, login/register, user management, all of it gone
- PostgreSQL and JPA — replaced by a lightweight local JSON index file
- Redis and rate limiting — completely unnecessary for a solo local tool
- Deep metadata extraction that was never actually used in search

Here's what replaced it:
- GitHub PAT in a `application.props` file — one line of config, no OAuth flow, no callback URLs
- GitHub Gists as storage — one Gist per snippet, no database required
- A local JSON index loaded into a `HashMap` on startup — search runs in-memory, instant
- A proper frontend built to spec, bundled directly into the Spring Boot JAR

The backend went from a multi-service architecture to a single Spring Boot app with no external dependencies at runtime. Memory usage dropped significantly. Setup went from "spin up Docker, run migrations, configure Redis" to "add your GitHub PAT, run the JAR."

The finish line for this challenge was wiring the frontend into the JAR so users get a single artifact — no separate frontend process, no Node.js required to run it. That's the piece that finally made it feel done.

---

## My Experience with GitHub Copilot

I used GitHub Copilot for the entire frontend. The backend was written by hand, but the React/TypeScript side was a Copilot-first workflow throughout with careful review by me.

The starting point was a detailed spec — layout decisions, color palette, typography choices, which components to use, how each view should behave. Having that spec meant Copilot had real context to work with rather than guessing at intent.

Where it helped most:
- **Scaffolding the three-column layout** — sidebar, snippet list, main panel — and getting the shadcn/ui components wired up correctly from the start
- **CodeMirror 6 integration** — the API is not the most intuitive, and Copilot got the read-only viewer and the editor both working with the right extensions without much back and forth
- **The diff view** — since the backend already returns parsed `DiffLine` objects with `ADDED`, `REMOVED`, `UNCHANGED` types, the frontend just needs to render them. Copilot handled the line-by-line rendering with the red/green overlay cleanly
- **The compare flow** — the command palette-style modal with an inline search bar filtering the snippet list was generated almost entirely from a description of the interaction

The spec-first approach made a real difference. Copilot is significantly more useful when it has a clear picture of what you're trying to build — visual style, component choices, interaction patterns. Vague prompts got vague results; specific prompts with context got production-quality components.

## Running ASTronaut
Running ASTronaut is simple — just make sure you have Java 21+ installed, then:
- Clone the repository
- Move to the root folder of the project
- Run `.\startup.bat`

**Frontend Repository:** https://github.com/kusoroadeolu/astronaut-ui
**Backend Repository:** https://github.com/kusoroadeolu/ASTronaut