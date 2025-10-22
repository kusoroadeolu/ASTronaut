# Astronaut - Complete Implementation Guide

## Overview

Astronaut is a full-stack code snippet management and analysis platform that allows users to store, organize, search, and compare Java code snippets. The system automatically extracts structural metadata from code using JavaParser and provides flexible search capabilities with optional fuzzy matching. The application features a modern, responsive frontend with syntax highlighting, markdown rendering, and side-by-side diff comparison.

---

## Architecture

### High-Level Components

**Backend:**
- **Controller Layer** — REST endpoints handling HTTP requests, parameter validation, and response formatting
- **Service Layer** — Business logic for CRUD operations, searching, parsing, and comparisons
- **Repository Layer** — Data access using Spring Data JPA with custom specifications for complex queries
- **Parsing Layer** — JavaParser-based code analysis and metadata extraction via visitor pattern
- **Security Layer** — JWT authentication with HTTP-only cookies, role-based authorization (APP_USER, APP_ADMIN)
- **Rate Limiting Layer** — Redis-backed IP-based rate limiting to prevent API abuse

**Frontend:**
- **Landing Page** — Marketing page showcasing features
- **Authentication** — Login/register page with tab-based switching
- **Dashboard** — Grid view of snippets with advanced search/filter capabilities
- **Snippet Detail** — View, edit, and compare individual snippets with syntax highlighting
- **Settings** — User profile management and preferences
- **Shared Components** — Reusable header, toast notifications, and styling

---

## Database Schema

### AppUser Entity

Stores user account information and preferences.

**Columns:**
- `app_user_id` (PK) — Unique user identifier, auto-generated
- `username` — User's display name, max 30 characters
- `email` — Unique email address, max 70 characters
- `password` — BCrypt-hashed password, max 100 characters
- `role` — User role (APP_USER or APP_ADMIN)
- `isDeleted` — Soft delete flag (default: false)
- `createdAt` — Account creation timestamp, set via @PrePersist
- `enableFuzzySearch` — Boolean preference for fuzzy search (default: false)

**Relationships:**
- One-to-many with Snippet (cascade delete with orphan removal)

### Snippet Entity

Stores code snippets and their extracted metadata.

**Columns:**
- `id` (PK) — Unique snippet identifier, auto-generated
- `name` — Snippet name, max 50 characters
- `content` — Actual code content, max 10,000 characters (trimmed on input)
- `language` — Enum (JAVA, OTHER)
- `isDraft` — Draft status (default: true)
- `extraNotes` — User notes, max 500 characters
- `metaDataAvailable` — Flag indicating if parsing succeeded
- `created_at` — Creation timestamp, set via @PrePersist
- `updated_at` — Last update timestamp, set via @PrePersist and @PreUpdate
- `app_user_id` (FK) — Reference to owning user

**ElementCollection Fields** (stored in join tables):
- `tags` — User-defined tags (Set<String>)
- `classNames` — Extracted class names (Set<String>)
- `classAnnotations` — Class-level annotations (Set<String>)
- `classFields` — Field types (Set<String>)
- `classFieldAnnotations` — Field-level annotations (Set<String>)
- `methodReturnTypes` — Method return types (Set<String>)
- `methodAnnotations` — Method-level annotations (Set<String>)

**Relationships:**
- Many-to-one with AppUser (lazy loaded)

---

## Authentication & Authorization

### User Roles

**APP_USER** — Standard user role. Can create, read, update, delete their own snippets and manage their account.

**APP_ADMIN** — Administrative role. Same permissions as APP_USER (role-based access is checked with hasAnyRole('APP_USER', 'APP_ADMIN')).

### Authentication Flow

1. User registers via POST `/auth/register` with username, email, password
2. System validates credentials and hashes password using Spring Security's BCrypt
3. User is created with APP_USER role and isDeleted=false
4. JWT token is generated and set as HTTP-only cookie
5. User logs in via POST `/auth/login` with email and password
6. System validates credentials against stored hash
7. New JWT token is generated and set as cookie
8. Subsequent requests include JWT in cookie for authentication
9. User logs out via DELETE `/users/logout` (invalidates token server-side)

### Authorization

All endpoints except `/auth/register`, `/auth/login`, and frontend static pages require authentication and APP_USER or APP_ADMIN role. Authorization is enforced at the controller level via `@PreAuthorize` annotations.

---

## Rate Limiting

### Implementation

Rate limiting is implemented using Redis to track request counts per IP address.

**RateLimitFilter** — Custom filter that intercepts all requests before JWT validation, checks the client's IP address against Redis store, and throws `RateLimitException` if limits are exceeded.

**Configuration:**
- `rate-limit.req-per-minute` in `application.yml` sets the maximum requests per minute per IP
- Uses `RedisTemplate` with `StringRedisSerializer` for keys and `GenericJackson2JsonRedisSerializer` for values
- Filter is placed before `JwtFilter` in the Spring Security filter chain

**Behavior:**
- Each request increments a counter for the client's IP with 1-minute expiration
- If counter exceeds configured limit, `RateLimitException` is thrown (HTTP 429)
- Rate limit applies to all endpoints including public ones

---

## Snippet Lifecycle

### Creation

1. User sends POST `/snippets` with snippetName, optional language, optional tags
2. `SnippetCrudServiceImpl.createSnippet()` creates Snippet entity with isDraft=true, metaDataAvailable=false
3. Content is trimmed before persistence
4. Snippet is persisted to database with @PrePersist setting createdAt and updatedAt
5. Snippet ID is returned immediately to user

### Metadata Extraction (Async)

1. After creation, if language is JAVA, `SnippetParser.parseSnippetContent()` is triggered asynchronously
2. Parser attempts to parse content using JavaParser's StaticJavaParser.parse()
3. If parsing succeeds, `VisitorOrchestrator.visitAllVisitors()` runs all 6 visitors to extract metadata
4. If parsing fails, parser wraps content in a public class and retries
5. If retry succeeds, "Wrapper" class name is removed from extracted metadata
6. If retry fails, metaDataAvailable is set to false and error is logged
7. Snippet is updated with extracted metadata and metaDataAvailable flag

### Visitor Pattern

Six visitors extract different metadata types:

- **ClassNameVisitor** — Extracts all class/interface names
- **ClassAnnotationVisitor** — Extracts class-level annotations
- **ClassFieldVisitor** — Extracts field types (not names)
- **ClassFieldAnnotationVisitor** — Extracts field-level annotations
- **MethodAnnotationVisitor** — Extracts method-level annotations
- **MethodReturnTypeVisitor** — Extracts method return types

All visitors inherit from `VoidVisitorAdapter<Set<String>>` and are orchestrated by `VisitorOrchestrator`.

### Update

1. User sends PUT `/snippets/{id}` with updated snippetName, language, tags, content, extraNotes
2. `SnippetCrudServiceImpl.updateSnippet()` fetches snippet and applies changes
3. Content is trimmed before persistence
4. @PreUpdate hook sets updatedAt to current time
5. If language is JAVA, parsing is triggered again asynchronously to re-extract metadata
6. User gets response immediately; metadata updates happen in background

### Deletion

1. User sends DELETE `/snippets/{id}`
2. `SnippetCrudServiceImpl.deleteSnippet()` uses custom repository method returning Integer to delete
3. Deletion checks that snippet belongs to requesting user before deleting
4. Returns 204 No Content on success

---

## Search & Filtering

### Search Types

The system supports two search modes toggled by user preference `enableFuzzySearch`:

**Direct Search** — Exact matches only. Uses `DirectSnippetSpecBuilder` to create JPA specifications with equality checks and `IN` clauses. Fast and precise.

**Fuzzy Search** — Partial/LIKE matches. Uses `FuzzySnippetSpecBuilder` with `LIKE %value%` wildcards on both sides. More forgiving but slower.

### Search Flow

1. User sends POST `/snippets/filter` with SearchCriteria (languages, tagsOrNames, classAnnotations, classNames, classFields, classFieldAnnotations, methodReturnTypes, methodAnnotations)
2. `SnippetQueryServiceImpl.searchBasedOnCriteria()` fetches user preferences
3. Builds specification using `DirectSnippetSpecBuilder` regardless of preference
4. If fuzzy search is enabled, also builds specification using `FuzzySnippetSpecBuilder`
5. Combines both specs with `Specification.anyOf()` (OR logic) if fuzzy enabled
6. Executes query via `SnippetRepository.findAll(specification, pageable)`
7. Maps results to SnippetPreview DTOs (includes updatedAt field) and returns paginated response

### Specification Building

Specifications use JPA Criteria API to construct type-safe queries:

```java
// Direct: exact match for tag "spring"
root.join("tags").in("spring");

// Fuzzy: partial match for tag containing "spr"
root.join("tags").like("%spr%");
```

All string comparisons are case-insensitive (converted to lowercase).

---

## Diff Comparison

### Comparison Flow

1. User sends GET `/snippets/{id}/compare/{comparingToId}`
2. `SnippetDiffServiceImpl.generateSnippetDiff()` fetches both snippets
3. Splits content by newlines into List<String>
4. Uses `difflib` library (UnifiedDiffUtils) to generate unified diff format
5. Parses unified diff output to extract line-by-line changes
6. Returns `SnippetDiffPair` with two SnippetDiff objects

### Change Types

Each line is marked as one of:
- **UNCHANGED** — Line exists identically in both snippets
- **ADDED** — Line present in "comparingTo" snippet but not "comparing"
- **REMOVED** — Line present in "comparing" snippet but not "comparingTo"

### Parsing Logic

Raw unified diff output is parsed by iterating through lines and checking first character:
- `' '` (space) → UNCHANGED
- `'+'` → ADDED
- `'-'` → REMOVED

Patch headers (@@) and file markers (---, +++) are skipped. Line numbers are tracked separately for each snippet.

---

## Frontend Architecture

### Pages

**Landing Page (index.html)**
- Marketing page showcasing platform features
- Hero section with call-to-action
- Feature cards highlighting key capabilities (metadata extraction, advanced search, diff comparison, tag organization)
- Minimal, clean design with gradient decorations
- Responsive layout

**Authentication (auth.html)**
- Tab-based interface switching between login and register
- Form validation with real-time feedback
- Password visibility toggle
- Session storage for user data after successful authentication
- Toast notifications for success/error messages
- Redirects to dashboard after authentication

**Dashboard (dashboard.html)**
- Grid view of snippet cards with metadata badges
- Compact search filters panel (language, tags/names, class annotations, class names, field types, field annotations, method return types, method annotations)
- Pagination controls
- Create new snippet button
- Toast notifications for operations
- Displays username in header
- Responsive grid layout

**Snippet Detail (snippet-detail.html)**
- Three modes: Read-only, Edit, and Diff comparison
- Read-only mode shows:
    - Snippet metadata (title, language badge, tags, created/updated dates)
    - Syntax-highlighted code with line numbers
    - Markdown-rendered notes
    - Action buttons (Edit, Delete, Compare, Back)
- Edit mode shows:
    - Editable form fields for all snippet properties
    - Large textareas for code and notes
    - Save/Delete/Cancel buttons
- Diff mode shows:
    - Side-by-side comparison with synchronized scrolling
    - Color-coded additions (green) and removals (red)
    - Line numbers for both versions
- Syntax highlighting via highlight.js (atom-one-dark theme)
- Markdown rendering via marked.js
- Copy code button functionality

**Settings (settings.html)**
- Account management section (username, email with inline editing)
- Password change section with visibility toggles
- Preferences section (fuzzy search toggle switch)
- Danger zone with account deletion
- Confirmation modal for destructive actions
- Toast notifications for all operations
- Responsive form layout

### Shared Components

**Header (header.css, header.js)**
- Fixed header with gradient logo ("Astronaut")
- Reusable `renderHeader()` function with configurable buttons
- Pre-defined button configurations: settings, back, logout
- Username display option
- Responsive design with mobile breakpoints
- Consistent across all pages

**Toast Notifications (toast.css, toast.js)**
- Centralized `showToast()` function for notifications
- Four types: success (green), error (red), warning (yellow), info (blue)
- Auto-dismiss after 3 seconds
- Stacked notifications in top-right corner
- Icon support via Remix Icon
- Smooth animations (slide in from right, fade out)

### Styling Approach

**Global Styles:**
- System font stack (-apple-system, BlinkMacSystemFont, Segoe UI)
- Dark theme with gradient backgrounds (#0f172a to #1e293b)
- Color palette using Tailwind-inspired colors
- Consistent spacing and border radius
- Glassmorphism effects (backdrop-filter blur)

**Component Styles:**
- Dedicated CSS files for each major page (dashboard.css, settings.css, snippet-detail.css)
- Shared header and toast styles
- Hover effects and transitions (0.3s ease)
- Box shadows for depth
- Responsive breakpoints at 768px and 1200px

### JavaScript Architecture

**Modular Structure:**
- Separate JS file for each page (dashboard.js, settings.js, snippet-detail.js)
- Shared header and toast utilities
- API calls using fetch with credentials: 'include' for JWT cookies
- Session storage for user data
- Error handling with try-catch and toast notifications

**Key Patterns:**
- Async/await for API calls
- Dynamic DOM manipulation
- Event delegation for dynamic content
- Form validation before submission
- Loading states and error recovery

### External Libraries

**Remix Icon** — Icon library for UI elements (4.6.0 via CDN)

**highlight.js** — Syntax highlighting for code blocks (11.9.0, atom-one-dark theme)

**marked.js** — Markdown parsing for snippet notes (11.1.1)

**difflib** — Backend library for diff generation (not in frontend)

---

## Error Handling

### Backend Exception Hierarchy

**AppUserAlreadyExistsException** (409 Conflict) — Email already registered during signup

**NoSuchAppUserException** (404 Not Found) — User not found by ID

**NoSuchSnippetException** (404 Not Found) — Snippet not found by ID or doesn't belong to user

**AppUserPersistenceException** (500 Internal Server Error) — Database error during user operations

**SnippetPersistenceException** (500 Internal Server Error) — Database error during snippet operations

**SnippetParseException** (500 Internal Server Error) — JavaParser failed to parse snippet even after wrapping

**InvalidCredentialsException** (401 Unauthorized) — Wrong password or email during login

**JwtException** (500 Internal Server Error) — JWT token validation/generation failed

**RateLimitException** (429 Too Many Requests) — Rate limit exceeded for IP address

### GlobalExceptionHandler

Spring's `@RestControllerAdvice` catches exceptions and returns standardized `ApiError` responses:

```json
{
  "status": 400,
  "message": "Human readable error message",
  "thrownAt": "2024-10-20T15:30:00Z"
}
```

### Frontend Error Handling

All API calls wrapped in try-catch blocks with toast notifications for errors. HTTP status codes determine toast type (error for 4xx/5xx, success for 2xx). Session expiration (401) triggers redirect to login page.

---

## Key Design Patterns

**Builder Pattern** — `DirectSnippetSpecBuilder` and `FuzzySnippetSpecBuilder` build complex JPA specifications fluently

**Factory Pattern** — `SnippetSpecBuilder.typeOf()` factory method creates appropriate builder based on search mode

**Visitor Pattern** — Six visitor classes traverse JavaParser AST to extract metadata from code

**Strategy Pattern** — Swap between direct and fuzzy search strategies based on user preference

**Data Transfer Objects (DTOs)** — Controllers work with DTOs (SnippetCreationRequest, SnippetResponse, SearchCriteria) rather than entities. Mapper converts between layers

**Async Processing** — Parsing happens asynchronously via `@Async` to avoid blocking user requests

**Component Composition** — Reusable header and toast components reduce duplication across frontend pages

---

## Performance Considerations

**Backend:**
- **Lazy Loading** — Snippet's reference to AppUser is lazy-loaded to avoid N+1 queries when fetching multiple snippets
- **Pagination** — All list endpoints use pagination (default 20 items) to limit result set size
- **ElementCollections** — Metadata is stored in separate join tables for efficient querying with JPA joins
- **Async Parsing** — Heavy parsing work happens in background thread pool to keep response times fast
- **Redis Caching** — Rate limit counters stored in Redis with automatic expiration
- **Content Trimming** — Snippet content trimmed on input to prevent excessive data storage

**Frontend:**
- **Code Splitting** — Separate CSS/JS files per page reduce initial load
- **CDN Resources** — External libraries loaded from CDN for caching benefits
- **Lazy Rendering** — Syntax highlighting and markdown parsing only on needed elements
- **Debounced Search** — Could be added to search inputs to reduce API calls
- **Session Storage** — User data cached locally to minimize API calls

---

## Data Validation

### Backend Validation

Request validation uses Jakarta Validation (formerly javax.validation):

**Username** — Required, minimum 1 character

**Email** — Required, valid email format, minimum 1 character, must be unique

**Password** — Required, 6-100 characters

**Snippet Name** — Required, minimum 1 character

**Tags** — Optional, must be unique within snippet (Set enforces uniqueness)

**Language** — Optional, enum constraint (JAVA or OTHER)

All validation happens at DTO level with `@Valid` annotations on controller method parameters.

### Frontend Validation

**Email Format** — Regex validation before submission

**Password Matching** — Confirmation field must match password

**Required Fields** — Form validation prevents submission with empty required fields

**Password Length** — Minimum 6 characters enforced

**Toast Feedback** — Validation errors displayed via toast notifications

---

## Security Features

1. **JWT Authentication** — Stateless authentication with HTTP-only cookies
2. **BCrypt Password Hashing** — Industry-standard password encryption
3. **CSRF Protection** — Built into Spring Security
4. **Rate Limiting** — IP-based rate limiting to prevent abuse
5. **Role-Based Access Control** — User and admin roles (though functionally equivalent)
6. **Content Sanitization** — Input trimming and length limits
7. **Session Management** — Logout invalidates tokens server-side
8. **XSS Protection** — Proper escaping in frontend (textContent vs innerHTML)

---

## Configuration

### Application Properties (application.yml)

```yaml
rate-limit:
  req-per-minute: 60  # Configurable rate limit per IP

spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### Web Security Configuration

- Permits public access to: `/auth/**`, frontend pages (`/`, `/index.html`, `/auth.html`, etc.), Swagger UI
- Requires authentication for all other endpoints
- JWT filter before standard authentication
- Rate limit filter before JWT filter

---

## Development Setup

### Prerequisites

- Java 17+
- Spring Boot 3.x
- PostgreSQL (or configured database)
- Redis
- Maven

### Running Locally

1. Configure database and Redis connection in `application.yml`
2. Run `mvn spring-boot:run`
3. Access frontend at `http://localhost:80/`
4. API documentation at `http://localhost:80/swagger-ui.html` (if configured)

---

## Future Enhancements

- Support for additional languages (Python, JavaScript, Go, etc.) with language-specific parsers
- Collaborative snippets with sharing and permissions
- Custom metadata fields defined by users
- Full-text search on content using database features
- Snippet templates and boilerplate management


---

## Project Timeline

**Total Development Time:** 10 days

**Day 1-7:** Backend development (Spring Boot, JWT auth, JavaParser integration, JPA specifications, diff comparison, rate limiting)

**Day 8-10:** Frontend development (5 pages, shared components, styling, integration)

**Key Learning:** JPA refresh, frontend UX considerations, full-stack integration