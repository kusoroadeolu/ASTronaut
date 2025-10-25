# ASTronaut Changelog

## [Unreleased]

### Added
- **Snippet Management**: Full CRUD for code snippets + metadata
    - Rest endpoints for saving, editing, deleting, grabbing snippets
    - Pagination so you're not drowning in results
    - Draft status for snippets you haven't finished yet

- **Java Code Parsing & Metadata Extraction**
    - Async parsing with JavaParser 
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
    - `FuzzySnippetSpecBuilder` for typos and partial matches
    - Toggle fuzzy search on/off per user
    - Case-insensitive across everything
    - `SnippetPreview` so queries don't fetch unnecessary data
    - New search endpoint

- **Snippet Comparison & Diff Viewer**
    - Side by side diff comparison between two snippets
    - Line by line change detection using Myers diff algorithm
    - Unified diff parsing into structured format
    - Change categorization: added, removed, unchanged
    - Diff service with unit and integration testing
  
- **User Account Management**
    - Account deletion with soft-delete via `isDeleted` flag
    - Async permanent data removal with scheduled job fallback
    - User preference updates (fuzzy search toggle, etc.)
    - Password confirmation during registration for better security
    - Enhanced validation flows for registration and deletion


### Changed
- Cleaned up auth code
- Moved `@Transactional` to actual implementations instead of interfaces
- Changed DDL strategy from `create` to `update`
- `Snippet.tags` is now `@ElementCollection`
- All parsed metadata is lowercase for consistent searching
- - Improved user registration flow with password confirmation validation
- Strengthened deletion flow with better security checks

### More Details
- Switched from `javaparser-symbol-solver-core` to just `javaparser-core`
- Redis + Spring Cache for caching principals
- Virtual threads doing the async work
- Custom exceptions for cleaner error handling
- DB schema updates: `enableFuzzySearch` added to users, tweaked `tags` and `metaDataAvailable` types
- Integrated java-diff-utils for robust diff algorithm implementation
- UnifiedDiffUtils for parsing and generating standardized diff format

## [Released]
### 10/25/2025
- Fixed an issue that caused a class cast exception when fetching data from redis
- Fixed a front end issue where the advanced search filter sent values to the wrong search criteria fields
- Fixed an issue where you couldn't create snippets if you didn't pick a language. Now it just defaults to `SnippetLanguage.OTHER`
- Fixed an issue where JavaParser didn't parse Java based snippets above JAVA 8
- Switched the column definition for snippet content to TEXT
