# ASTronaut Changelog

## [Unreleased]

### Added
- **Snippet Management**: Full CRUD for code snippets + metadata
    - Rest endpoints for saving, editing, deleting, grabbing snippets
    - Pagination so you're not drowning in results
    - Draft status for snippets you haven't finished yet

- **Java Code Parsing & Metadata Extraction**
    - Async parsing with JavaParser (it won't block your thread)
    - Automatically get all java code based metadata i.e. class names, annotations, methods, return types, all that
    - Wraps code fragments so they actually parse
    - Virtual threads for even faster async stuff

- **Auth & Security**
    - JWT tokens stored in secure HTTP-only cookies (no XSS nonsense)
    - Auto token refresh so you don't get logged out randomly
    - Redis caching to speed things up
    - Request validation so bad data doesn't sneak through
    - Role-based access control for endpoints

- **Search That Actually Works**
    - Search by tags, names, languages, and code structure
    - `DirectSnippetSpecBuilder` for exact matches
    - `FuzzySnippetSpecBuilder` for typos and partial stuff
    - Toggle fuzzy search on/off per user
    - Case-insensitive across everything
    - `SnippetPreview` so queries don't fetch unnecessary data
    - New search endpoint

### Changed
- Cleaned up auth code 
- Moved `@Transactional` to actual implementations instead of interfaces
- Changed DDL strategy from `create` to `update` 
- `Snippet.tags` is now `@ElementCollection` 
- All parsed metadata is lowercase for consistent searching

### Technical Details
- Switched from `javaparser-symbol-solver-core` to just `javaparser-core`
- Redis + Spring Cache for caching principals
- Virtual threads doing the async work
- Custom exceptions for cleaner error handling
- DB schema updates: `enableFuzzySearch` added to users, tweaked `tags` and `metaDataAvailable` types