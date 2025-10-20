# Astronaut API Documentation

## Overview

The Astronaut API provides comprehensive endpoints for user authentication, account management, and code snippet management with advanced search and comparison features.

**Authentication:** JWT token (set as cookie on login/register)  
**Authorization:** Requires `APP_USER` or `APP_ADMIN` role

---

## Error Responses

All endpoints may return error responses in this format:

```json
{
  "status": 400,
  "message": "Human readable error message",
  "thrownAt": "2024-10-20T15:30:00Z"
}
```

Common status codes across all endpoints:
- **401** — User is not authenticated
- **404** — Resource not found
- **500** — An unexpected error occurred

---

## Authentication

### Register a New User
**POST** `/auth/register`

Creates a new user account.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "confirmPassword": "SecurePass123"
}
```

**Validation:**
- `username` — Required, minimum 1 character
- `email` — Required, valid email format, minimum 1 character
- `password` — Required, 6-100 characters
- `confirmPassword` — Required, must match password

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

**Possible Responses:**
- **201 Created** — User registered, JWT cookie set
- **400 Bad Request** — Passwords don't match
- **409 Conflict** — Email already exists
- **500 Internal Server Error** — Registration error

---

### Log In a User
**POST** `/auth/login`

Authenticates a user and returns JWT token.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**Validation:**
- `email` — Required, valid email format
- `password` — Required, 6-100 characters

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com"
}
```

**Possible Responses:**
- **200 OK** — User authenticated, JWT cookie set
- **401 Unauthorized** — Invalid email or password
- **500 Internal Server Error** — Login error

---

## User Management

### Get All Snippets (for current user)
**GET** `/snippets`

Retrieves paginated snippets for authenticated user.

**Query Parameters:**
- `page` — Zero-based page index (default: 0)
- `size` — Page size (default: 20, minimum: 1)
- `sort` — Sorting criteria, format: `property,(asc|desc)` (default: `createdAt,DESC`)

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Spring Bean Configuration",
      "language": "JAVA",
      "tags": ["spring", "beans"],
      "createdAt": "2024-10-20T10:00:00Z"
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

**Possible Responses:**
- **200 OK** — Snippets retrieved successfully
- **401 Unauthorized** — User not authenticated
- **500 Internal Server Error** — Retrieval error

---

### Get a Snippet by ID
**GET** `/snippets/{id}`

Retrieves a specific snippet.

**Path Parameters:**
- `id` — Snippet ID (required)

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Spring Bean Configuration",
  "content": "public class AppConfig { ... }",
  "language": "JAVA",
  "tags": ["spring", "beans"],
  "extraNotes": "Used for autowiring setup",
  "isDraft": false,
  "createdAt": "2024-10-20T10:00:00Z",
  "lastUpdated": "2024-10-20T12:00:00Z"
}
```

**Possible Responses:**
- **200 OK** — Snippet found
- **404 Not Found** — Snippet not found
- **401 Unauthorized** — User not authenticated
- **500 Internal Server Error** — Retrieval error

---

### Create a Snippet
**POST** `/snippets`

Creates a new code snippet.

**Request Body:**
```json
{
  "snippetName": "Spring Bean Configuration",
  "language": "JAVA",
  "tags": ["spring", "beans"]
}
```

**Validation:**
- `snippetName` — Required, minimum 1 character
- `language` — Optional, enum: `JAVA`, `OTHER`
- `tags` — Optional, array of unique strings

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Spring Bean Configuration",
  "language": "JAVA",
  "tags": ["spring", "beans"],
  "isDraft": true,
  "createdAt": "2024-10-20T10:00:00Z",
  "lastUpdated": "2024-10-20T10:00:00Z"
}
```

**Possible Responses:**
- **201 Created** — Snippet created successfully
- **401 Unauthorized** — User not authenticated
- **500 Internal Server Error** — Creation error

---

### Update a Snippet
**PUT** `/snippets/{id}`

Updates snippet metadata and content.

**Path Parameters:**
- `id` — Snippet ID (required)

**Request Body:**
```json
{
  "snippetName": "Updated Name",
  "language": "JAVA",
  "content": "updated code content",
  "tags": ["spring", "updated"],
  "extraNotes": "Updated notes"
}
```

**Validation:**
- `snippetName` — Required, minimum 1 character
- `tags` — Required, array of unique strings
- `language` — Optional, enum: `JAVA`, `OTHER`
- `content` — Optional, string
- `extraNotes` — Optional, string

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Updated Name",
  "content": "updated code content",
  "language": "JAVA",
  "tags": ["spring", "updated"],
  "extraNotes": "Updated notes",
  "isDraft": false,
  "createdAt": "2024-10-20T10:00:00Z",
  "lastUpdated": "2024-10-20T14:00:00Z"
}
```

**Possible Responses:**
- **200 OK** — Snippet updated successfully
- **404 Not Found** — Snippet not found
- **401 Unauthorized** — User not authenticated
- **500 Internal Server Error** — Update error

---

### Delete a Snippet
**DELETE** `/snippets/{id}`

Deletes a snippet permanently.

**Path Parameters:**
- `id` — Snippet ID (required)

**Response:**
- **204 No Content** — Snippet deleted successfully

**Possible Responses:**
- **204 No Content** — Snippet deleted
- **404 Not Found** — Snippet not found
- **401 Unauthorized** — User not authenticated
- **500 Internal Server Error** — Deletion error

---

## Snippet Analytics

### Filter Snippets
**GET** `/snippets/filter`

Searches and filters snippets based on criteria.

**Query Parameters:**
- `page` — Zero-based page index (default: 0)
- `size` — Page size (default: 20, minimum: 1)
- `sort` — Sorting criteria (default: `createdAt,DESC`)

**Request Body:**
```json
{
  "languages": ["JAVA"],
  "tagsOrNames": ["spring", "beans"],
  "classAnnotations": ["Component", "Service"],
  "classNames": ["AppConfig"],
  "classFields": ["applicationContext"],
  "classFieldAnnotations": ["Autowired"],
  "methodReturnTypes": ["void", "String"],
  "methodAnnotations": ["Override", "PostConstruct"]
}
```

All fields are optional and accept arrays of unique strings. Each represents a search criterion.

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Spring Bean Configuration",
      "language": "JAVA",
      "tags": ["spring", "beans"],
      "createdAt": "2024-10-20T10:00:00Z"
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 50,
    "totalPages": 3
  }
}
```

**Possible Responses:**
- **200 OK** — Snippets filtered successfully
- **401 Unauthorized** — User not authenticated
- **500 Internal Server Error** — Filter error

---

### Compare Two Snippets
**GET** `/snippets/{id}/compare/{comparingToId}`

Generates a diff between two snippets.

**Path Parameters:**
- `id` — First snippet ID (required)
- `comparingToId` — Second snippet ID (required)

**Response (200 OK):**
```json
{
  "comparing": {
    "snippetName": "Spring Bean Configuration",
    "lines": [
      {
        "lineNum": 1,
        "lineContent": "public class AppConfig {",
        "lineType": "UNCHANGED"
      },
      {
        "lineNum": 2,
        "lineContent": "  @Bean",
        "lineType": "REMOVED"
      }
    ]
  },
  "comparingTo": {
    "snippetName": "Spring Bean Configuration v2",
    "lines": [
      {
        "lineNum": 1,
        "lineContent": "public class AppConfig {",
        "lineType": "UNCHANGED"
      },
      {
        "lineNum": 2,
        "lineContent": "  @Component",
        "lineType": "ADDED"
      }
    ]
  }
}
```

**Line Types:**
- `UNCHANGED` — Line exists in both snippets
- `ADDED` — Line added in comparingTo
- `REMOVED` — Line removed from comparing

**Possible Responses:**
- **200 OK** — Snippets compared successfully
- **404 Not Found** — One or both snippets not found
- **401 Unauthorized** — User not authenticated
- **500 Internal Server Error** — Comparison error

---

### Update User Preferences
**PUT** `/users/preferences`

Updates user preferences like fuzzy search toggle.

**Request Body:**
```json
{
  "enableFuzzySearch": true
}
```

**Response (200 OK):**
```json
{
  "isFuzzySearchEnabled": true
}
```

**Possible Responses:**
- **200 OK** — Preferences updated
- **401 Unauthorized** — User not authenticated
- **404 Not Found** — User not found
- **500 Internal Server Error** — Update error

---

### Update Username or Email
**PUT** `/users/me`

Updates username or email for authenticated user.

**Request Body:**
```json
{
  "username": "new_username",
  "email": "newemail@example.com"
}
```

**Validation:**
- `username` — Required, minimum 1 character
- `email` — Required, valid email format, minimum 1 character

**Response:**
- **200 OK** — User details updated

**Possible Responses:**
- **200 OK** — Details updated successfully
- **409 Conflict** — Email already exists
- **401 Unauthorized** — User not authenticated
- **404 Not Found** — User not found
- **500 Internal Server Error** — Update error

---

### Update Password
**PUT** `/users/me/password`

Updates the authenticated user's password.

**Request Body:**
```json
{
  "currentPassword": "CurrentPass123",
  "newPassword": "NewPass456",
  "confirmNewPassword": "NewPass456"
}
```

**Validation:**
- All fields required
- Passwords must be 6-100 characters
- `confirmNewPassword` must match `newPassword`

**Response:**
- **200 OK** — Password updated

**Possible Responses:**
- **200 OK** — Password updated successfully
- **400 Bad Request** — Invalid current password or passwords don't match
- **401 Unauthorized** — User not authenticated
- **404 Not Found** — User not found
- **500 Internal Server Error** — Update error

---

### Log Out Current User
**DELETE** `/users/logout`

Logs out the authenticated user.

**Response:**
- **204 No Content** — User logged out

**Possible Responses:**
- **204 No Content** — Logged out successfully
- **401 Unauthorized** — User not authenticated
- **404 Not Found** — User not found
- **500 Internal Server Error** — Logout error

---

### Delete User Account
**DELETE** `/users`

Permanently deletes the authenticated user's account.

**Request Body:**
```json
{
  "password": "CurrentPassword123",
  "confirmPassword": "CurrentPassword123"
}
```

**Response:**
- **204 No Content** — Account deleted

**Possible Responses:**
- **204 No Content** — Account deleted successfully
- **400 Bad Request** — Password confirmation doesn't match
- **401 Unauthorized** — User not authenticated
- **404 Not Found** — User not found
- **500 Internal Server Error** — Deletion error

---

## Notes

- All endpoints require authentication except `/auth/register` and `/auth/login`
- Authenticated endpoints require valid JWT token (automatic via cookie after login/register)
- Pagination defaults to 20 items per page sorted by creation date (newest first)
- Tags are case-sensitive and must be unique within a request
- Snippet content and extra notes are optional and can be empty strings