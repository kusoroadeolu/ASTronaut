# Astronaut API - Implementation Guide

## Overview

Astronaut is a code snippet management and analysis platform that allows users to store, organize, search, and compare Java code snippets. The system automatically extracts structural metadata from code using JavaParser and provides flexible search capabilities with optional fuzzy matching.

---

## Architecture

### High-Level Components

The system is organized into these main layers:

**Controller Layer** — REST endpoints handling HTTP requests, parameter validation, and response formatting.

**Service Layer** — Business logic for CRUD operations, searching, parsing, and comparisons.

**Repository Layer** — Data access using Spring Data JPA with custom specifications for complex queries.

**Parsing Layer** — JavaParser-based code analysis and metadata extraction via visitor pattern.

**Security Layer** — JWT authentication, role-based authorization (APP_USER, APP_ADMIN).

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
- `isDeleted` — Soft delete flag (currently not actively used)
- `createdAt` — Account creation timestamp, set via @PrePersist
- `enableFuzzySearch` — Boolean preference for fuzzy search (default: false)

**Relationships:**
- One-to-many with Snippet (cascade delete with orphan removal)

### Snippet Entity

Stores code snippets and their extracted metadata.

**Columns:**
- `id` (PK) — Unique snippet identifier, auto-generated
- `name` — Snippet name, max 50 characters
- `content` — Actual code content, max 10,000 characters
- `language` — Enum (JAVA, OTHER)
- `isDraft` — Draft status (default: true)
- `extraNotes` — User notes, max 100 characters
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
3. User is created with APP_USER role
4. JWT token is generated and set as HTTP-only cookie
5. User logs in via POST `/auth/login` with email and password
6. System validates credentials against stored hash
7. New JWT token is generated and set as cookie
8. Subsequent requests include JWT in cookie for authentication
9. User logs out via DELETE `/users/logout` (invalidates token server-side)

### Authorization

All endpoints except `/auth/register` and `/auth/login` require authentication and APP_USER or APP_ADMIN role. Authorization is enforced at the controller level via `@PreAuthorize` annotations.

---

## Snippet Lifecycle

### Creation

1. User sends POST `/snippets` with snippetName, optional language, optional tags
2. `SnippetCrudServiceImpl.createSnippet()` creates Snippet entity with isDraft=true, metaDataAvailable=false
3. Snippet is persisted to database with @PrePersist setting createdAt and updatedAt
4. Snippet ID is returned immediately to user

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
- **ClassFieldVisitor** — Extracts field names
- **ClassFieldAnnotationVisitor** — Extracts field-level annotations
- **MethodAnnotationVisitor** — Extracts method-level annotations
- **MethodReturnTypeVisitor** — Extracts method return types

All visitors inherit from `VoidVisitorAdapter<Set<String>>` and are orchestrated by `VisitorOrchestrator`.

### Update

1. User sends PUT `/snippets/{id}` with updated snippetName, language, tags, content, extraNotes
2. `SnippetCrudServiceImpl.updateSnippet()` fetches snippet and applies changes
3. @PreUpdate hook sets updatedAt to current time
4. If language is JAVA, parsing is triggered again asynchronously to re-extract metadata
5. User gets response immediately; metadata updates happen in background

### Deletion

1. User sends DELETE `/snippets/{id}`
2. `SnippetCrudServiceImpl.deleteSnippet()` uses custom repository method to delete
3. Deletion checks that snippet belongs to requesting user before deleting
4. Returns 204 No Content on success

---

## Search & Filtering

### Search Types

The system supports two search modes toggled by user preference `enableFuzzySearch`:

**Direct Search** — Exact matches only. Uses `DirectSnippetSpecBuilder` to create JPA specifications with equality checks and `IN` clauses. Fast and precise.

**Fuzzy Search** — Partial/LIKE matches. Uses `FuzzySnippetSpecBuilder` with `LIKE %value%` wildcards on both sides. More forgiving but slower.

### Search Flow

1. User sends GET `/snippets/filter` with SearchCriteria (languages, tagsOrNames, classAnnotations, classNames, classFields, classFieldAnnotations, methodReturnTypes, methodAnnotations)
2. `SnippetQueryServiceImpl.searchBasedOnCriteria()` fetches user preferences
3. Builds specification using `DirectSnippetSpecBuilder` regardless of preference
4. If fuzzy search is enabled, also builds specification using `FuzzySnippetSpecBuilder`
5. Combines both specs with `Specification.anyOf()` (OR logic) if fuzzy enabled
6. Executes query via `SnippetRepository.findAll(specification, pageable)`
7. Maps results to SnippetPreview DTOs and returns paginated response

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

## Error Handling

### Exception Hierarchy

**AppUserAlreadyExistsException** (409 Conflict) — Email already registered during signup.

**NoSuchAppUserException** (404 Not Found) — User not found by ID.

**NoSuchSnippetException** (404 Not Found) — Snippet not found by ID or doesn't belong to user.

**AppUserPersistenceException** (500 Internal Server Error) — Database error during user operations.

**SnippetPersistenceException** (500 Internal Server Error) — Database error during snippet operations.

**SnippetParseException** (500 Internal Server Error) — JavaParser failed to parse snippet even after wrapping.

**InvalidCredentialsException** (401 Unauthorized) — Wrong password or email during login.

**JwtException** (500 Internal Server Error) — JWT token validation/generation failed.

### GlobalExceptionHandler

Spring's `@RestControllerAdvice` catches exceptions and returns standardized `ApiError` responses:

```json
{
  "status": 400,
  "message": "Human readable error message",
  "thrownAt": "2024-10-20T15:30:00Z"
}
```

---

## Key Design Patterns

**Builder Pattern** — `DirectSnippetSpecBuilder` and `FuzzySnippetSpecBuilder` build complex JPA specifications fluently.

**Factory Pattern** — `SnippetSpecBuilder.typeOf()` factory method creates appropriate builder based on search mode.

**Visitor Pattern** — Six visitor classes traverse JavaParser AST to extract metadata from code.

**Strategy Pattern** — Swap between direct and fuzzy search strategies based on user preference.

**Data Transfer Objects (DTOs)** — Controllers work with DTOs (SnippetCreationRequest, SnippetResponse, SearchCriteria) rather than entities. Mapper converts between layers.

**Async Processing** — Parsing happens asynchronously via `@Async` to avoid blocking user requests.

---

## Performance Considerations

**Lazy Loading** — Snippet's reference to AppUser is lazy-loaded to avoid N+1 queries when fetching multiple snippets.

**Pagination** — All list endpoints use pagination (default 20 items) to limit result set size.

**ElementCollections** — Metadata is stored in separate join tables for efficient querying with JPA joins.

**Case-Insensitive Indexes** — Consider database-level lowercase indexes on tags, classNames, etc. for faster searches.

**Async Parsing** — Heavy parsing work happens in background thread pool to keep response times fast.

**Spec Caching** — Specifications are built per-request but could be cached if search patterns are stable.

---

## Data Validation

Request validation uses Jakarta Validation (formerly javax.validation):

**Username** — Required, minimum 1 character

**Email** — Required, valid email format, minimum 1 character, must be unique

**Password** — Required, 6-100 characters

**Snippet Name** — Required, minimum 1 character

**Tags** — Optional, must be unique within snippet (Set enforces uniqueness)

**Language** — Optional, enum constraint (JAVA or OTHER)

All validation happens at DTO level with `@Valid` annotations on controller method parameters.

---

## Future Enhancements

- Support for additional languages (Python, JavaScript, Go, etc.) with language-specific parsers
- Advanced diff visualization with side-by-side highlighting
- Snippet versioning and history tracking
- Collaborative snippets with sharing and permissions
- Custom metadata fields defined by users
- Full-text search on content using database features
- Snippet templates and boilerplate management
- Integration with code review tools

No java script yet we're just making the prompt for the UI rn yk. So we will iterate over the UI till we get something good before we get the JS. So we need a landing page, a login page, a page where users can see their snippets. Also a search bar for names and tags and another for advanced search(the metadata). Also we need a way for users to compare two snippets. Also another screen where users can logout, delete their account, toggle fuzzy search and update their username, password and email .So yeah I want us to iterate over it so we can fully understand what we want the UI to look like before we create the prompt. Do you understand?