# ASTronaut - README

## Overview
ASTronaut(emphasis on the AST) was basically built by me to organize my java snippets without needing to go to GitHub everytime. Yes, there are probably better snippet organizers out there, with better UI and better features, but basically I just needed something that could allow me to search my java snippets based on certain metadata and compare two snippets 

## Who is ASTronaut for
Basically java devs who want to be able to search their code based on certain metadata in their code, and have code snippets locally without having to go online to retrieve them.
</br> To set up ASTronaut locally view [**setup.md**](setup.md)
</br> ASTronaut will also be hosted on the cloud later on


## Architecture

### High-Level Components

This section basically includes the basic structure of the web app

**Backend:**
The backend basically consists of the normal spring layering pattern: 
- Controllers -> Services -> Repository
- Also, some utility classes to reduce boilerplate in the service classes.
- And certain special services which use java parser to extract the needed metadata and diff-utils to get the diffs between two snippets

**Frontend:**
The frontend consists of five pages:
- Landing page -> Basically everything you'll see on a landing page
- Auth page -> Login/Register page, nothing too fancy here
- Dashboard page -> Here you can create a snippet, view your snippets, search through them and also navigate to the settings page or just click a snippet to perform some more actions on it
- Snippet-detail page -> Here you can perform CRUD operations on a snippet and also compare it with other snippets. Also has syntax highlighting for code and markdown formatting for extra notes(added these just to make it fancy yk)
- Settings page -> Here you can update your info and toggle fuzzy search

---

## Database Schema

The DB schema is very simple. Just an app user entity which has a one to may relationship with the snippet entity.

### AppUser Entity
This stores the app user's info.

```java
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "app_user_id")
    private Long id;

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 70)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "role", nullable = false)
    private AppUserRole role;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Snippet> snippet = new ArrayList<>();

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "enableFuzzySearch")
    private Boolean enableFuzzySearch;
```


### Snippet Entity

Stores code snippets and their extracted metadata, also mapped to a user.

**Columns:**
```java
@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "draft")
    private boolean isDraft = true;

    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private SnippetLanguage language;

    @Column(name = "content", length = 10000)
    private String content = "";

    @Column(name = "extra_notes", length = 500)
    private String extraNotes = "";

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    private Set<String> classNames = new HashSet<>();

    @ElementCollection
    private Set<String> classAnnotations = new HashSet<>();

    @ElementCollection
    private Set<String> classFields = new HashSet<>();

    @ElementCollection
    private Set<String> classFieldAnnotations = new HashSet<>();

    @ElementCollection
    private Set<String> methodReturnTypes = new HashSet<>();

    @ElementCollection
    private Set<String> methodAnnotations = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "metaDataAvailable")
    private Boolean metaDataAvailable;
```
#### Some details on this
- The metadata available flag was actually added a while back for debugging, but I forgot to remove it and now its there I guess
- The isDraft flag is also pretty useless too I'll definitely remove it later, but it's honestly harmless rn
- The metadata collected from the snippet content is pretty barebones, it works for me though

---

## Authentication & Authorization

### User Roles

The two user roles are **APP_USER** and **APP_ADMIN**, they're pretty identical right now. The admin role is just there in case I actually add functionality for it later.

### Authentication Flow & Authorization
The authentication flow is pretty simple. You can either log in or register. Simple as that
</br> JWT tokens is the auth token for this app. The current expiry time if you're using this locally should be around 300 million milliseconds/83 hours. When I deploy it, it will be around 10 minutes for a token
</br> Basic user metadata is cached after login/registration and on each subsequent request, the authenticated user object `UserPrincipal` is rebuilt from the cached metadata. I did this basically to prevent subsequent db hits on each request. I'm honestly not sure if this is a valid approach. It does work for me though
</br> All endpoints except `/auth/register`, `/auth/login` and some frontend pages are authenticated.

---
## Rate Limiting

### Implementation

Rate limiting was implemented using redis and a sliding window algorithm to track requests.

**RateLimitFilter** - This is my custom filter that ensures a user hasn't exceeded their rate limits. This filter was placed before my JwtFilter, just because I believe if you've exceeded your rate limits you shouldn't be authenticated
</br> Also, the default requests per minute per IP in my `application.yml` is 60 requests. You can edit it as you please

---

## Some more details on Snippets

### Metadata Extraction (Async)
After a snippet is updated, if the language is **JAVA**, the parsing is offloaded to a virtual thread, to prevent blocking the main thread. Also, I'm not too sure if parsing is CPU intensive work, else I might need to disable virtual threads for that

**How is this metadata extracted?**
</br>Honestly, the java parser library does all the heavy lifting, I just use visitors, to extract the metadata from the snippet and save it to the DB.
</br>If the snippet parsing fails, I usually rewrap in a dummy class, just to reparse, in case the user only posted a method or field. This wrapper class isn't included as metadata
</br>All my visitors inherit from `VoidVisitorAdapter<Set<String>>` and are orchestrated by `VisitorOrchestrator`.


---

## Search & Filtering
This is honestly where the most learning occurred for me in this project. I haven't handled dynamic search queries with different criteria before so this was definitely new territory for me to step into.
</br>For the dynamic search queries, I used spring data specifications. This was honestly very unintuitive to use especially with element collections + the lack of detailed docs was definitely something else
</br>I did implement two search modes direct(exact search matches) and fuzzy search matches using wildcards `LIKE` keywords. My implementations definitely weren't the best, and I'm sure there are some N + 1 queries hiding in there, but it works at least for now lol. 
</br>So on the frontend, users can enable fuzzy searches, they are disabled by default. Fuzzy searches combine the snippets of direct searches AND those found with wildcards.

```java
// Direct: exact match for tag "spring"
root.join("tags").in("spring");

// Fuzzy: partial match for tag containing "spr"
root.join("tags").like("%spr%");
```


## Diff Comparison

This was mainly added just for fun it honestly serves no concrete purpose in this project like my other features but it seemed fun to add, so I added it.

### What this does
It basically allows you to compare you to compare changes/diffs between two snippets i.e. comparing(the snippet we're comparing), comparingTo(the snippet we're comparing against). 
**QUICK NOTE** : Comparing is basically the original snippet. Comparing to is like the updated snippet. Both snippets don't need to be related to be compared but hopefully this explanation is understandable lol
</br>These unified diffs are generated by `java-diff-utils`. No need to reinvent the wheel honestly
</br>Each line is marked as one of:
- **UNCHANGED** — Line exists identically in both snippets
- **ADDED** — Line present in "comparingTo" snippet but not "comparing"
- **REMOVED** — Line present in "comparing" snippet but not "comparingTo"

### How I normalized the diffs
The raw unified diff output is parsed by iterating through lines and checking each character
- `' '` (space) → UNCHANGED -> Both comparing and comparing to lines are incremented
- `'+'` → ADDED -> Only comparing to lines is incremented
- `'-'` → REMOVED -> Only comparing lines is incremented

Patch headers (@@) and file markers (---, +++) are skipped. 


## Error Handling
The exception hierarchy here is very simple and straight forward
### Exception Hierarchy

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
Spring's `@RestControllerAdvice` catches exceptions and returns an `ApiError` response

```json
{
  "status": 400,
  "message": "Error msg",
  "thrownAt": "2024-10-20T15:30:00Z"
}
```

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

## Development Setup

### Prerequisites
**These are the prerequisites to run this locally**
- Java 17+
- Spring Boot 3.x
- PostgresSQL (or configured database)
- Redis (Or you can use the docker-compose file provided to spin up your own redis instance locally)
- Maven

### Running Locally
1. Configure database and Redis connection(or just use the docker-compose file to spin up your own redis instance locally) in `application.yml`
2. Run `mvn spring-boot:run`
3. Access frontend at `http://localhost:80/`
4. API documentation at `http://localhost:80/swagger-ui.html`


## Hopeful Future Enhancements
- Support for additional languages (Python, JavaScript, Go, etc.) with language-specific parsers
- Full-text search on content using database features(Apache Lucene? Probably tbh)
- Snippet templates and boilerplate management



## Project Timeline
**Total Development Time:** 10 days
**Key Things I learnt:** JPA and Specifications. Honestly I didn't learn much from this past specifications. It was a relatively simple project      