# ASTronaut Changelog

## [Unreleased]

### Added
- **Snippet Management**: Full CRUD operations for code snippets with metadata support
    - REST endpoints for creating, updating, deleting, and retrieving snippets
    - Pagination support for user snippet lists
    - Draft status for incomplete snippets

- **Java Code Parsing & Metadata Extraction**
    - Asynchronous parsing of Java snippets using JavaParser
    - Automatic extraction of structural metadata (class names, annotations, fields, methods, return types)
    - Fallback wrapping logic for code fragments
    - Virtual thread support for improved async performance

- **Authentication & Security**
    - JWT-based authentication with secure HTTP-only cookies
    - Automatic token refresh capability
    - Redis caching of user principals for improved performance
    - Request validation using Jakarta Bean Validation
    - Role-based endpoint protection with `@PreAuthorize`

### Changed
- Refactored auth logic to reduce duplication in token generation and caching
- Moved `@Transactional` annotations from interfaces to implementation methods
- Changed JPA DDL strategy from `create` to `update` for safer migrations

### Technical Details
- Replaced `javaparser-symbol-solver-core` with `javaparser-core`
- Integrated Spring Cache and Redis for principal caching
- Configured virtual threads for async operations
- Added custom exceptions for better error handling